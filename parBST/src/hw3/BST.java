package hw3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.Callable;

/**
 * **************************************************************************
 * The sequential Binary Search Tree (for storing int values)
 *
 *****************************************************************************/

public class BST implements Runnable, Callable<Boolean> {
	private static final Node root = new Node(0);
	public static final int INSERT_NUMBER = 1000000;
	public static final int INSERT_SEARCH_NUMBER = 1000000;
	public static final int MAXIMUM = 10000;
	public static final int TEST_TIMES = 10;
	
	private static Node backup_root = null;

	private Act act;
	private int data;

	LockType read_lock;
	LockType write_lock;

	public BST(Act act, int data, LockType rw) {
		this.act = act;
		this.data = data;
		if (rw == LockType.NORW) {
			read_lock = LockType.LOCK;
			write_lock = LockType.LOCK;
		} else if (rw == LockType.RW) {
			read_lock = LockType.RDLOCK;
			write_lock = LockType.WRLOCK;
		}
	}

	public BST() {
		this.act = Act.TRAVERSAL;
		this.data = 0;
	}

	@Override
	public void run() {
		if (act == Act.INSERT) {
			insert();
		} else if (act == Act.SEARCH) {
			search();
		} else if (act == Act.DELETE) {
			delete();
		}
	}

	@Override
	public Boolean call() throws Exception {
		return search();
	}

	public static void clean_backup(boolean is_backup) {
		if (is_backup)
			backup_root = root.right;

		root.right = null;
	}

	public static void recovery() {
		root.right = backup_root;
	}

	/*****************************************************
	 *
	 * INSERT
	 *
	 ******************************************************/
	public void insert() {
		root.lock(write_lock);
		if (root.right == null) {
			root.right = new Node(data);
			root.unlock(write_lock);
			return ;
		}
		
		root.right.lock(write_lock);
		Node pre = root;
		Node cur = root.right;
		while (true) {
			if (cur.data > data) {
				if (cur.left == null) {
					cur.left = new Node(data);
					pre.unlock(write_lock);
					cur.unlock(write_lock);
					
					break;
				}
				
				Node suc = pre;
				pre = cur;
				cur = cur.left;
				
				cur.lock(write_lock);
				suc.unlock(write_lock);
			} else if (cur.data < data) {
				if (cur.right == null) {
					cur.right = new Node(data);
					pre.unlock(write_lock);
					cur.unlock(write_lock);
					
					break;
				}
				
				Node suc = pre;
				pre = cur;
				cur = cur.right;
				
				cur.lock(write_lock);
				suc.unlock(write_lock);
			} else {
				pre.unlock(write_lock);
				cur.unlock(write_lock);
				break;
			}
		}
	}

	/*****************************************************
	 *
	 * SEARCH
	 *
	 ******************************************************/
	public boolean search() {
		root.lock(read_lock);
		if (root.right == null) {
			root.unlock(read_lock);
			return false;
		}
		
		root.right.lock(read_lock);
		Node pre = root;
		Node cur = root.right;
		while (true) {
			if (cur.data > data) {
				if (cur.left == null) {
					pre.unlock(read_lock);
					cur.unlock(read_lock);
					
					return false;
				}
				
				Node suc = pre;
				pre = cur;
				cur = cur.left;
				
				cur.lock(read_lock);
				suc.unlock(read_lock);
			} else if (cur.data < data) {
				if (cur.right == null) {
					pre.unlock(read_lock);
					cur.unlock(read_lock);
					
					return false;
				}
				
				Node suc = pre;
				pre = cur;
				cur = cur.right;
				
				cur.lock(read_lock);
				suc.unlock(read_lock);
			} else {
				pre.unlock(read_lock);
				cur.unlock(read_lock);
				return true;
			}
		}
	}

	/*****************************************************
	 *
	 * DELETE
	 *
	 ******************************************************/

	public void delete() {
		root.lock(write_lock);
		if (root.right == null) {
			root.unlock(write_lock);
			return ;
		}
		
		root.right.lock(write_lock);
		Node pre = root;
		Node cur = root.right;
		while(true) {
			if (cur.data > data) {
				if (cur.left == null) {
					pre.unlock(write_lock);
					cur.unlock(write_lock);
					
					break;
				}
				
				Node suc = pre;
				pre = cur;
				cur = cur.left;
				
				cur.lock(write_lock);
				suc.unlock(write_lock);
			} else if (cur.data < data) {
				if (cur.right == null) {
					pre.unlock(write_lock);
					cur.unlock(write_lock);
					
					break;
				}
				
				Node suc = pre;
				pre = cur;
				cur = cur.right;
				
				cur.lock(write_lock);
				suc.unlock(write_lock);
			} else {
				if (cur.left == null) {
					if (pre.data > cur.data)
						pre.left = cur.right;
					else
						pre.right = cur.right;
					
					pre.unlock(write_lock);
					cur.unlock(write_lock);
					
					break;
				} else if (cur.right == null) {
					if (pre.data > cur.data)
						pre.left = cur.left;
					else
						pre.right = cur.left;
					
					pre.unlock(write_lock);
					cur.unlock(write_lock);
					
					break;
				} else {
					cur.data = retrieveData(cur.left);
					cur.left = deleteWithNoLock(cur.left, cur.data);
					
					pre.unlock(write_lock);
					cur.unlock(write_lock);
					
					break;
				}
			}
		}
	}

	private Node deleteWithNoLock(Node cur_p, int data) {
		if (cur_p == null) {
			return cur_p;
		} else if (data < cur_p.data) {
			if (cur_p.left != null) {
				cur_p.left = deleteWithNoLock(cur_p.left, data);
			}
		} else if (data > cur_p.data) {
			if (cur_p.right != null) { 
				cur_p.right = deleteWithNoLock(cur_p.right, data);
			}
		} else {
			if (cur_p.left == null) {
				return cur_p.right;
			} else if (cur_p.right == null) {
				return cur_p.left;
			} else {
				// get data from the rightmost node in the left subtree
				cur_p.data = retrieveData(cur_p.left);

				// delete the rightmost node in the left subtree
				cur_p.left = deleteWithNoLock(cur_p.left, cur_p.data);
			}
		}

		return cur_p;
	}

	private int retrieveData(Node p) {
		while (p.right != null) {
			p = p.right;
		}
		return p.data;
	}
	
	// you don't need to implement hand-over-hand lock for this function.
	public int findMin() {
		if (root.right == null) {
			throw new RuntimeException("cannot findMin.");
		}
		Node n = root.right;
		while (n.left != null) {
			n = n.left;
		}
		return n.data;
	}

	public static boolean checkResult(String result_filename) {
		Vector<Integer> data_set = getDataSet(root.right);
		try (BufferedReader result_file = new BufferedReader(new FileReader(result_filename))) {
			String check_num = result_file.readLine();
			if (data_set == null && Integer.parseInt(check_num) == 0)
				return true;

			for (int x : data_set) {
				check_num = result_file.readLine();
				if (x != Integer.parseInt(check_num)) {
					System.out.println(x + " " + check_num);
					return false;
				}
			}
		} catch (IOException e) {
			System.out.println("result file error");
			return false;
		}

		return true;
	}

	private static Vector<Integer> getDataSet(Node p) {
		Vector<Integer> data_set = new Vector<Integer>();

		if (p != null) {
			if (p.left != null)
				data_set.addAll(getDataSet(p.left));

			data_set.add(p.data);

			if (p.right != null)
				data_set.addAll(getDataSet(p.right));

			return data_set;
		} else
			return null;
	}

	/*************************************************
	 *
	 * TRAVERSAL
	 *
	 **************************************************/

	public void preOrderTraversal() {
		preOrderHelper(root.right);
	}

	private void preOrderHelper(Node r) {
		if (r != null) {
			System.out.print(r + " ");
			preOrderHelper(r.left);
			preOrderHelper(r.right);
		}
	}

	public void inOrderTraversal() {
		inOrderHelper(root.right);
	}

	private void inOrderHelper(Node r) {
		if (r != null) {
			inOrderHelper(r.left);
			System.out.print(r + " ");
			inOrderHelper(r.right);
		}
	}

	public static boolean check() {
		return helpcheck(root.right);
	}

	private static boolean helpcheck(Node r) {
		if (r != null) {
			if (helpcheck(r.left))
				return true;

			if (r.check())
				return true;

			if (helpcheck(r.right))
				return true;
		}
		return false;
	}
}
