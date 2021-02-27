import java.util.Random;
public class testFairUnifanBathroom implements Runnable {
	final static int PEOPLE = 500;
	final static Random rand = new Random();
	
	final FairUnifanBathroom bathroom;
	
	public testFairUnifanBathroom(FairUnifanBathroom bathroom) {
		this.bathroom = bathroom;
	}
	
	public void run() {
		int i = rand.nextInt(2);
		try{
			if (i == 0) {
				System.out.println("UT: Waiting");
				bathroom.enterBathroomUT();
				System.out.println("UT: Entered");
			}
			else {
				System.out.println("OU: Waiting");
				bathroom.enterBathroomOU();
				System.out.println("OU: Entered");
			}
			Thread.sleep(rand.nextInt(100));
			if (i == 0) {
				System.out.println("UT: Leaving Bathroom");
				bathroom.leaveBathroomUT();
			}
			else {
				System.out.println("OU: Leaving Bathroom");
				bathroom.leaveBathroomOU();
			}
		}
		catch (InterruptedException e) {}
		//int index = -1;

		/*for (int round = 0; round < PEOPLE; ++round) {
			System.out.println("Thread " + Thread.currentThread().getId() + " is WAITING round:" + round);
			try {
				index = gate.await();
				System.out.println(index);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Thread " + Thread.currentThread().getId() + " is leaving round:" + round);
		}*/
	}
	
	public static void main(String[] args) {
		FairUnifanBathroom bathroom = new FairUnifanBathroom();
		Thread[] t = new Thread[PEOPLE];
		
		for (int i = 0; i < PEOPLE; ++i) {
			t[i] = new Thread(new testFairUnifanBathroom(bathroom));
		}
		
		for (int i = 0; i < PEOPLE; ++i) {
			t[i].start();
		}
    }
}
