/*
 * UT-EID=dmb4377
 * UT-EID=ji4399
 * 
 */
import java.util.concurrent.Semaphore; // for implementation using Semaphores

public class CyclicBarrier {
	int parties;
	Semaphore sem1;
	Semaphore sem2;
	Semaphore sem3;
	static int index = 0;
	static boolean sem1Full = false;
	static boolean sem2Full = false;
	
	public CyclicBarrier(int parties) {
		this.parties = parties;
		this.sem1 = new Semaphore(parties);
		this.sem2 = new Semaphore(parties);
		this.sem3 = new Semaphore(1);
	}
	
	public int await() throws InterruptedException {
		int retVal = index;
		sem1.acquire();
		if (sem1.availablePermits() == 0) {
		   sem2Full = false;
		   sem1Full = true;
		}

		sem3.acquire(); //synchronize incrementing index
		index += 1;
		sem3.release();
		// wait for threads to arrive
		while (! sem1Full){
		   Thread.sleep(1);
		}
		sem1.release();

		//Stage 2
		sem2Full = false;
		sem2.acquire();
		if (sem2.availablePermits() == 0) {
		   	index = 0;
		   	sem1Full = false;
		   	sem2Full = true;
		}
		// block
		while (! sem2Full) {
			Thread.sleep(1);
		}
		sem2.release();
		return retVal;
	}
}
