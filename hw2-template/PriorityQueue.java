
// ji4399
// dmb4377

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PriorityQueue {
	private final Node head;
	private final Node tail;
	private final AtomicInteger size;
	private final int MAX_SIZE;
	private final ReentrantLock signalLock;
	private final Condition notEmpty;

	private class Node {
		String name;
		int priority;
		boolean isDeleted;
		Node next;
		final ReentrantLock lock;

		Node() {
			this.isDeleted = false;
			this.next = null;
			this.lock = new ReentrantLock();
		}

		Node(String name, int priority) {
			this.name = name;
			this.priority = priority;
			this.isDeleted = false;
			this.next = null;
			this.lock = new ReentrantLock();
		}
	}

	// Creates a Priority queue with maximum allowed size as capacity
	public PriorityQueue(int maxSize) {
		head = new Node();
		tail = new Node();
		head.next = tail;
		size = new AtomicInteger();
		this.MAX_SIZE = maxSize;
		signalLock = new ReentrantLock();
		notEmpty = signalLock.newCondition();
	}

	// Adds the name with its priority to this queue.
	// Returns the current position in the list where the name was inserted;
	// otherwise, returns -1 if the name is already present in the list.
	// This method blocks when the list is full.
	public int add(String name, int priority) {
		Node newNode = new Node(name, priority);
		Node predecessor = this.head;
		Node successor = this.head.next;
		int index = 0;
		for (;;) {
			if (successor == tail || successor.priority < priority) {
				predecessor.lock.lock();
				try {
					successor.lock.lock();
					try {
						if (!predecessor.isDeleted && !successor.isDeleted && predecessor.next == successor) {
							if (size.getAndIncrement() < MAX_SIZE) {
								newNode.next = successor;
								predecessor.next = newNode;
								signalLock.lock();
								notEmpty.signal();
								signalLock.unlock();
								return index;
							} else {
								// retry
								size.getAndDecrement();
								predecessor.lock.unlock();
								successor.lock.unlock();
								predecessor = this.head;
								successor = this.head.next;
							}
						} else {
							// retry
							predecessor.lock.unlock();
							successor.lock.unlock();
							predecessor = this.head;
							successor = this.head.next;
						}
					} finally {
						if (successor.lock.isLocked()) successor.lock.unlock();
					}
				} finally {
					if (predecessor.lock.isLocked()) predecessor.lock.unlock();
				}
			}
			predecessor = successor;
			successor = successor.next;
			index++;
		}
	}

	// Returns the position of the name in the list;
	// otherwise, returns -1 if the name is not found.
	public int search(String name) {
		int index = 0;
		Node current = this.head.next;
		while (current != this.tail) {
			if (!current.isDeleted && current.name.equals(name)) {
				return index;
			} else {
				current = current.next;
				index++;
			}
		}
		return -1;
	}

	// Retrieves and removes the name with the highest priority in the list,
	// or blocks the thread if the list is empty.
	public String getFirst() {
		String name;
		Node first;
		for (;;) {
			signalLock.lock();
			try {
				while (head.next == tail) {
					try {
						notEmpty.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} finally {
				signalLock.unlock();
			}
			this.head.lock.lock();
			try {
				first = this.head.next;
				first.lock.lock();
				try {
					if (!first.isDeleted && this.head.next == first) {
						name = first.name;
						first.isDeleted = true;
						this.head.next = first.next;
						size.getAndDecrement();
						return name;
					}
					// else retry
				} finally {
					first.lock.unlock();
				}
			} finally {
				this.head.lock.unlock();
			}
		}
	}
}

