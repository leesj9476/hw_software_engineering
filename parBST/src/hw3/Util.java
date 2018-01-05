package hw3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Util {
	public static boolean test(int threads, int works, String workload_filename, String result_filename) {
		if (!makeTestFile(threads, works, works, "workload.txt", "result.txt"))
			return false;

		ActSet[] workload_set;
		int work_size1 = 0;
		int work_size2 = 0;
		int thread_num = 0;
		try (BufferedReader workload_file = new BufferedReader(new FileReader(workload_filename))) {
			String[] argu_line = workload_file.readLine().split(" ");

			thread_num = Integer.parseInt(argu_line[0]);
			work_size1 = Integer.parseInt(argu_line[1]);
			work_size2 = Integer.parseInt(argu_line[2]);
			workload_set = new ActSet[thread_num * (work_size1 + work_size2)];

			for (int i = 0; i < thread_num * (work_size1 + work_size2); i++) {
				String act_argu[] = workload_file.readLine().split(" ");

				Act act = Act.getAct(Integer.parseInt(act_argu[0]));
				int num = Integer.parseInt(act_argu[1]);
				workload_set[i] = new ActSet(act, num);
			}
		} catch (FileNotFoundException e1) {
			System.out.println("file not found error");
			return false;
		} catch (IOException e1) {
			System.out.println("IO error");
			return false;
		}

		System.out.println("test with " + thread_num + " threads");
		ExecutorService threadpool = Executors.newFixedThreadPool(thread_num);
		for (int i = 0; i < thread_num * work_size1; i++) {
			Runnable r = new BST(workload_set[i].getAct(), workload_set[i].getNum(), LockType.NORW);
			threadpool.execute(r);
		}

		threadpool.shutdown();
		try {
			while (!threadpool.awaitTermination(5, TimeUnit.SECONDS))
				;
		} catch (InterruptedException e) {
			System.out.println("threadpool error");
			return false;
		}

		threadpool = Executors.newFixedThreadPool(thread_num);
		for (int i = thread_num * work_size1; i < thread_num * (work_size1 + work_size2); i++) {
			Runnable r = new BST(workload_set[i].getAct(), workload_set[i].getNum(), LockType.NORW);
			threadpool.execute(r);
		}

		threadpool.shutdown();
		try {
			while (!threadpool.awaitTermination(5, TimeUnit.SECONDS))
				;
		} catch (InterruptedException e) {
			System.out.println("threadpool error");
			return false;
		}

		boolean ISD_result = BST.checkResult(result_filename);
		if (!ISD_result) {
			System.out.println("isd fail");
			return false;
		}

		int search_works = works / 10;
		Vector<Integer> search_set = randomIntGenerate(search_works, BST.MAXIMUM);
		Vector<Integer> cur_result_set = readResultFile(result_filename);
		if (search_set == null || cur_result_set == null)
			return false;

		Vector<Boolean> search_result_set = new Vector<Boolean>(search_works);
		for (int i = 0; i < search_works; i++) {
			search_result_set.add((cur_result_set.indexOf(search_set.get(i)) != -1));
		}

		threadpool = Executors.newFixedThreadPool(thread_num);
		Set<SearchResult> result_future_set = new HashSet<SearchResult>(search_works);
		for (int i = 0; i < search_works; i++) {
			Callable<Boolean> r = new BST(Act.SEARCH, search_set.get(i), LockType.NORW);
			Future<Boolean> f = threadpool.submit(r);
			result_future_set.add(new SearchResult(i, f));
		}

		threadpool.shutdown();
		try {
			while (!threadpool.awaitTermination(5, TimeUnit.SECONDS))
				;
		} catch (InterruptedException e) {
			System.out.println("threadpool error");
			return false;
		}

		boolean S_result = true;
		for (SearchResult search_result : result_future_set) {
			try {
				if (search_result_set.get(search_result.idx) != search_result.future.get()) {
					S_result = false;
					break;
				}
			} catch (InterruptedException | ExecutionException e) {
				System.out.println("threadpool error");
				return false;
			}
		}

		if (!S_result) {
			System.out.println("search fail");
			return false;
		}

		return true;
	}

	public static boolean makeTestFile(int threads, int work1, int work2, String workload_filename,
			String result_filename) {
		File f1 = new File(workload_filename);
		File f2 = new File(result_filename);
		if (f1.exists())
			f1.delete();
		if (f2.exists())
			f2.delete();

		try (BufferedWriter workload_file = new BufferedWriter(new FileWriter(f1));
				BufferedWriter result_file = new BufferedWriter(new FileWriter(f2))) {
			String line = threads + " " + work1 + " " + work2;
			workload_file.write(line);

			int size = BST.MAXIMUM / 2;

			////////////////////////
			// make workload file //
			////////////////////////

			int test1_size = threads * work1;
			int test2_size = threads * work2;

			// insert and search (9:1)
			Vector<Integer> test1_nums = randomIntGenerate(test1_size, size);
			Vector<Integer> test1_acts = randomIntGenerate(test1_size, 10);
			for (int i = 0; i < test1_size; i++) {
				if (test1_acts.get(i) == 3)
					test1_acts.set(i, 1); // search
				else
					test1_acts.set(i, 0); // insert

				line = test1_acts.get(i) + " " + test1_nums.get(i);
				workload_file.newLine();
				workload_file.write(line);
			}

			// insert, search and delete
			// 0 ~ MAXIMUM/2 -> search and delete(5:5)
			// MAXIMUM/2 ~ MAXIMUM -> insert and search(9:1)
			Vector<Integer> test2_nums = randomIntGenerate(test2_size, BST.MAXIMUM);
			Vector<Integer> test2_acts = randomIntGenerate(test2_size, 10);
			for (int i = 0; i < test2_size; i++) {
				if (test2_nums.get(i) < size) {
					if (test2_acts.get(i) < 5)
						test2_acts.set(i, 1); // search
					else
						test2_acts.set(i, 2); // delete
				} else {
					if (test2_acts.get(i) == 3)
						test2_acts.set(i, 1); // search
					else
						test2_acts.set(i, 0); // insert
				}

				line = test2_acts.get(i) + " " + test2_nums.get(i);
				workload_file.newLine();
				workload_file.write(line);
			}

			//////////////////////
			// make result file //
			//////////////////////

			// make result Tree set
			int result_size = 0;
			Set<Integer> result_set = new TreeSet<Integer>();
			for (int i = 0; i < test1_size; i++) {
				if (test1_acts.get(i) == 0 && result_set.add(test1_nums.get(i)))
					result_size++;
			}

			for (int i = 0; i < test2_size; i++) {
				if (test2_acts.get(i) == 0 && result_set.add(test2_nums.get(i)))
					result_size++;
				else if (test2_acts.get(i) == 2 && result_set.remove(test2_nums.get(i))) {
					result_size--;
				}
			}

			// write tree set to file
			line = result_size + "";
			result_file.write(line);
			for (int x : result_set) {
				line = x + "";
				result_file.newLine();
				result_file.write(line);
			}

		} catch (IOException e) {
			System.out.println("file io error");

			return false;
		}

		return true;
	}

	public static Vector<Integer> readResultFile(String result_filename) {
		Vector<Integer> result_set;
		try (BufferedReader result_file = new BufferedReader(new FileReader(result_filename))) {
			String check_num = result_file.readLine();
			result_set = new Vector<Integer>(Integer.parseInt(check_num));
			while ((check_num = result_file.readLine()) != null) {
				result_set.add(Integer.parseInt(check_num));
			}
		} catch (IOException e) {
			System.out.println("result file error");
			return null;
		}

		return result_set;
	}

	public static double elapsedTime(long start, long end) {
		return (double) (end - start) / 1000000000.0;
	}

	public static Vector<Integer> randomIntGenerate(int size, int max) {
		Vector<Integer> rand_container = new Vector<Integer>(size);
		Random rand = new Random();
		for (int i = 0; i < size; i++)
			rand_container.add(rand.nextInt(max));

		return rand_container;
	}
}

class SearchResult {
	int idx;
	Future<Boolean> future;

	public SearchResult(int idx, Future<Boolean> future) {
		this.idx = idx;
		this.future = future;
	}
}
