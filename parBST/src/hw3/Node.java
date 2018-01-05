package hw3;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Node {
	private ReentrantLock node_lock;
	private ReentrantReadWriteLock node_rwlock;

	int data;
	Node left, right;

	public Node(int data, Node l, Node r) {
		this.data = data;
		left = l;
		right = r;
		node_lock = new ReentrantLock();
		node_rwlock = new ReentrantReadWriteLock();
	}

	public Node(int data) {
		this(data, null, null);
	}

	public String toString() {
		return "" + data;
	}

	public void lock(LockType l_act) {
		if (l_act == LockType.LOCK)
			node_lock.lock();
		else if (l_act == LockType.RDLOCK)
			node_rwlock.readLock().lock();
		else if (l_act == LockType.WRLOCK)
			node_rwlock.writeLock().lock();
	}

	public void unlock(LockType l_act) {
		if (l_act == LockType.LOCK)
			node_lock.unlock();
		else if (l_act == LockType.RDLOCK)
			node_rwlock.readLock().unlock();
		else if (l_act == LockType.WRLOCK)
			node_rwlock.writeLock().unlock();
	}
	
	public boolean check() {
		return node_lock.isLocked();
	}
}