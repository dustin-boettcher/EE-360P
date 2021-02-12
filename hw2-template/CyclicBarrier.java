/*
 * dmb4377
 * 
 */
import java.util.concurrent.Semaphore; // for implementation using Semaphores

public class CyclicBarrier {
	int parties;
	Semaphore sem1;
	Semaphore sem2;
	
	public CyclicBarrier(int parties) {
		this.parties = parties;
		this.sem1 = new Semaphore(parties);
		this.sem2 = new Semaphore(parties);
	}
	
	public int await() throws InterruptedException {
		
           int index = 0;
           sem1.acquire();
		   while (sem1.availablePermits() != 0){}
		   sem1.release();
		   sem2.acquire();
		   while (sem2.availablePermits() != 0) {}
		   sem2.release();
	    return index;
	}
}
