package hw3;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
	public static void main(String[] args) {
		boolean success = false;
		LF_LL.clean_backup(false);
		success = Util.test(8, 10000, "workload.txt", "result.txt");
		if (success)
			System.out.println("test success");
		else {
			System.out.println("test fail");
			System.exit(-1);
		}

		Vector<Integer> insert_rand = Util.randomIntGenerate(LF_LL.INSERT_NUMBER, LF_LL.MAXIMUM);

		int[] thread_num = { 1, 2, 4, 8 };
		int thread_modes = 4;
		double[] insert_test_time = new double[thread_modes];
		for (int i = 0; i < LF_LL.TEST_TIMES; i++) {
			for (int x = 0; x < thread_modes; x++) {
				ExecutorService threadpool = Executors.newFixedThreadPool(thread_num[x]);
				LF_LL.clean_backup(false);
				long start_time = System.nanoTime();
				for (int rand : insert_rand) {
					Runnable r = new LF_LL(Act.INSERT, rand);
					threadpool.execute(r);
				}

				threadpool.shutdown();
				try {
					while (!threadpool.awaitTermination(5, TimeUnit.SECONDS))
						;
				} catch (InterruptedException e) {
					System.out.println("threadpool error");
					System.exit(-1);
				}

				long end_time = System.nanoTime();
				double elapsed_sec = Util.elapsedTime(start_time, end_time);
				insert_test_time[x] += elapsed_sec;
			}

			System.out.print("# ");
		}

		System.out.println();
		LF_LL.clean_backup(true);

		for (int i = 0; i < thread_modes; i++) {
			insert_test_time[i] /= LF_LL.TEST_TIMES;
			System.out.println("INSERT " + LF_LL.INSERT_NUMBER + " numbers thread : " + thread_num[i]
					+ " elapsed time : " + String.format("%.8f", insert_test_time[i]));
		}

		Vector<Integer> insert_search_rand = Util.randomIntGenerate(LF_LL.INSERT_NUMBER, LF_LL.MAXIMUM);
		/*
		 * insert | search / 1 : 1 / 1 : 4 / 1 : 9
		 */
		int test_mode[] = { 1, 2, 3 };
		int test_modes = 3;
		double[][] insert_search_test_time = new double[thread_modes][test_modes];
		for (int c = 0; c < LF_LL.TEST_TIMES; c++) {
			for (int x = 0; x < thread_modes; x++) {
				for (int t = 0; t < test_modes; t++) {
					// the proportion of insert and search
					int insert_p = 1;
					int search_p = test_mode[t] * test_mode[t];

					ExecutorService threadpool = Executors.newFixedThreadPool(thread_num[x]);
					LF_LL.recovery();

					int cnt = 0;
					long start_time = System.nanoTime();
					for (int rand : insert_search_rand) {
						for (int i = 0; i < insert_p && cnt < LF_LL.INSERT_SEARCH_NUMBER; i++, cnt++) {
							Runnable r = new LF_LL(Act.INSERT, rand);
							threadpool.execute(r);
						}

						for (int i = 0; i < search_p && cnt < LF_LL.INSERT_SEARCH_NUMBER; i++, cnt++) {
							Runnable r = new LF_LL(Act.INSERT, rand);
							threadpool.execute(r);
						}
					}

					threadpool.shutdown();
					try {
						while (!threadpool.awaitTermination(5, TimeUnit.SECONDS))
							;
					} catch (InterruptedException e) {
						System.out.println("threadpool error");
						System.exit(-1);
					}

					long end_time = System.nanoTime();
					double elapsed_sec = Util.elapsedTime(start_time, end_time);
					insert_search_test_time[x][t] += elapsed_sec;

					LF_LL.clean_backup(false);
				}
			}

			System.out.print("# ");
		}

		System.out.println();

		for (int i = 0; i < thread_modes; i++) {
			for (int j = 0; j < test_modes; j++) {
				insert_search_test_time[i][j] /= LF_LL.TEST_TIMES;
				System.out.println("INSERT AND SEARCH " + LF_LL.INSERT_SEARCH_NUMBER + " numbers(1:"
						+ (test_mode[j] * test_mode[j]) + ") thread : " + thread_num[i] + " elapsed time : "
						+ String.format("%.8f", insert_search_test_time[i][j]));
			}
		}

		System.exit(0);
	}
}