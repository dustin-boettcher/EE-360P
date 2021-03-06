/*
 * UT-EID=dmb4377
 * UT-EID=ji4399
 * 
 */

public class MonitorCyclicBarrier {
	
	static int index = 0;
	static int parties;
	static int leaving;
	static Object o1;
	static Object o2;
	
	public MonitorCyclicBarrier(int parties) {
		MonitorCyclicBarrier.parties = parties;
		MonitorCyclicBarrier.leaving = 0;
		MonitorCyclicBarrier.o1 = new Object();
		MonitorCyclicBarrier.o2 = new Object();
	}
	
	public synchronized void decrementLeaving() {
		leaving--;
	}
	
	public static int waitForThreads() throws InterruptedException {
		int retVal;
		synchronized(o1) {
			if (index == parties)
				index = 0;
			retVal = index;
			++index;
			if (index == parties) {
				o1.notifyAll();
				leaving = parties - 1;
			}
			while ((index) < parties){
				o1.wait();
			}
		}
		return retVal;
	}
	
	public static void BlockFromEntering() throws InterruptedException {
		synchronized(o2) {
			if (leaving == 0) {
				o2.notifyAll();
			}
			while (leaving > 0) {
				o2.wait();
			}
		}
	}
	
	public int await() throws InterruptedException{
		int index = waitForThreads();
		decrementLeaving();
		BlockFromEntering();
		return index;
	}
	
}
