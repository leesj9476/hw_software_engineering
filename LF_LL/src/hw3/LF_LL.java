package hw3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;

public class LF_LL implements Runnable, Callable<Boolean> {
	private static final Node root;
	public static final int INSERT_NUMBER = 1000000;
	public static final int INSERT_SEARCH_NUMBER = 1000000;
	public static final int MAXIMUM = 10000;
	public static final int TEST_TIMES = 10;

	private static Node backup_node = null;

	private Act act;
	private int data;

	static {
		root = new Node(Integer.MIN_VALUE);
		root.setRoot();
		root.setNext(new Node(Integer.MIN_VALUE));
		root.getNext().getReference().setNext(new Node(Integer.MAX_VALUE));
		root.getNext().getReference().getNext().getReference().setNext(new Node(Integer.MAX_VALUE));
	}

	public LF_LL(Act act, int data) {
		this.act = act;
		this.data = data;
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
		if (is_backup) {
			backup_node = root.getNext().getReference().getNext().getReference();
		}

		root.getNext().getReference().setNext(new Node(Integer.MAX_VALUE));
		root.getNext().getReference().getNext().getReference().setNext(new Node(Integer.MAX_VALUE));
	}

	public static void recovery() {
		root.getNext().getReference().setNext(backup_node);
	}

	/*****************************************************
	 *
	 * FIND
	 *
	 ******************************************************/
	public NodeTuple find() {
		Node pre = null;
		Node cur = null;
		Node suc = null;
		boolean[] mark = { false };
		boolean check;

		retry: while (true) {
			pre = root;
			cur = pre.getNext().getReference();

			while (true) {
				suc = cur.getNext().get(mark);
				while (mark[0]) {
					check = pre.getNext().compareAndSet(cur, suc, false, false);
					if (check == false) {
						continue retry;
					}

					cur = suc;
					suc = cur.getNext().get(mark);
				}
				
				if (cur.getData() >= data)
					return new NodeTuple(pre, cur);

				pre = cur;
				cur = suc;
			}
		}
	}

	/*****************************************************
	 *
	 * INSERT
	 *
	 ******************************************************/
	public void insert() {
		while (true) {
			NodeTuple insert_pos = find();

			Node pre = insert_pos.getPreNode();
			Node cur = insert_pos.getCurNode();
			if (cur.getData() == data)
				break;

			Node node = new Node(data);
			node.setNext(cur);
			if (pre.getNext().compareAndSet(cur, node, false, false))
				break;
		}
	}

	/*****************************************************
	 *
	 * SEARCH
	 *
	 ******************************************************/
	public Boolean search() {
		Node cur = root.getNext().getReference().getNext().getReference();
		boolean[] mark = { false };

		while (cur.getData() < data)
			cur = cur.getNext().getReference();

		cur.getNext().get(mark);

		return (cur.getData() == data && mark[0] == false);
	}

	/*****************************************************
	 *
	 * DELETE
	 *
	 ******************************************************/
	public void delete() {
		while (true) {
			NodeTuple delete_pos = find();

			Node pre = delete_pos.getPreNode();
			Node cur = delete_pos.getCurNode();
			if (cur.getData() != data)
				break;

			Node suc = cur.getNext().getReference();
			boolean check = cur.getNext().attemptMark(suc, true);
			if (!check)
				continue;

			pre.getNext().compareAndSet(cur, suc, false, false);
			break;
		}
	}

	private class NodeTuple {
		private Node pre;
		private Node cur;

		NodeTuple(Node pre, Node cur) {
			this.pre = pre;
			this.cur = cur;
		}

		public Node getPreNode() {
			return pre;
		}

		public Node getCurNode() {
			return cur;
		}
	}

	public static boolean checkResult(String result_filename) {
		Node cur = root.getNext().getReference().getNext().getReference();
		try (BufferedReader result_file = new BufferedReader(new FileReader(result_filename))) {
			String check_num = result_file.readLine();
			while (cur.getData() != Integer.MAX_VALUE && (check_num = result_file.readLine()) != null) {
				if (cur.getData() != Integer.parseInt(check_num)) {
					System.out.println(cur.getData() + " " + Integer.parseInt(check_num));
					return false;
				}
				cur = cur.getNext().getReference();
			}
		} catch (IOException e) {
			System.out.println("result file error");
			return false;
		}

		return true;
	}
}
