/*
 * UT-EID=dmb4377
 * UT-EID=ji4399
 * 
 */

public class MonitorCyclicBarrier {
	
	static int index = 0;
	static int parties;
	static int leaving;
	static boolean endOfRound = false;
	static Object o1;
	static Object o2;
	
	public MonitorCyclicBarrier(int parties) {
		this.parties = parties;
		this.leaving = 0;
		this.o1 = new Object();
		this.o2 = new Object();
	}
	
	/*public int await() throws InterruptedException {
		while (leaving > 0) {
			Thread.sleep(1);
			
		}
        int retVal = updateIndex();
        System.out.println(index);
		waitForThreads();
		System.out.println("Left Section " + retVal + " " + index);
		//if (retVal == parties - 1)
			//leaving = parties - 1;
	
		//if (retVal == parties - 1) 
        	//index = 0;
          // you need to write this code
		decrementLeaving(retVal);
		updateIndex();
	    return retVal;
	}*/
	
	public synchronized int updateIndex() {
		if (! endOfRound) index++;
		if (leaving == 0 && endOfRound) {
			index = 0;
			endOfRound = false;
		}
		return index - 1;
	}
	
	public synchronized void decrementLeaving() {
		leaving--;
	}
	
	public static void waitForThreads() throws InterruptedException {
		synchronized(o1) {
			++index;
			if (index == parties - 1) {
				o1.notifyAll();
				leaving = parties - 1;
			}
			System.out.println("index is " + index);
			while (index < parties - 1){
				o1.wait();
			}
		}
		
	}
	
	public static void BlockFromEntering() throws InterruptedException {
		synchronized(o2) {
			if (leaving == 0) {
				o2.notifyAll();
				index = 0;
			}
			while (leaving > 0) {
				o2.wait();
			}
		}
	}
	
	public int await() throws InterruptedException{
		BlockFromEntering();
		waitForThreads();
		decrementLeaving();
		
		return 1;
	}
	
}
