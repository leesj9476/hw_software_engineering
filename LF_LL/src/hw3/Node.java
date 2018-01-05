package hw3;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class Node{
	private int data;
	private AtomicMarkableReference<Node> next;
	private boolean is_root;

	public Node(int data) {
		this.data = data;
		this.next = null;
		this.is_root = false;
	}

	public int getData() {
		return data;
	}
	
	public AtomicMarkableReference<Node> getNext() {
		return next;
	}
	
	public void setNext(Node next) {
		this.next = new AtomicMarkableReference<Node> (next, false);
	}
	
	public void setRoot() {
		this.is_root = true;
	}
	
	public Boolean getRoot() {
		return is_root;
	}
}
