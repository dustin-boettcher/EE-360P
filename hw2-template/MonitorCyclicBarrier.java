/*
 * EID's of group members
 * 
 */

public class MonitorCyclicBarrier {
	
	int index = 0;
	int parties;
	int leaving;
	
	public MonitorCyclicBarrier(int parties) {
		this.parties = parties;
		this.leaving = 0;
	}
	
	public int await() throws InterruptedException {
		while (leaving > 0) {}
        int retVal = updateIndex();
        
		while (index < parties - 1){}
		if (retVal == parties - 1)
			leaving = parties - 1;
	
		//if (retVal == parties - 1) 
        	//index = 0;
          // you need to write this code
		decrementLeaving();
		//if (leaving == 0)
			//index = 0;
		//while (leaving > 0) {}
	    return retVal;
	}
	
	public synchronized int updateIndex() {
		index++;
		return index - 1;
	}
	
	public synchronized int decrementLeaving() {
		leaving--;
		if (leaving == 0) index = 0;
		return leaving + 1;
	}
	
}
