//UT-EID=dmb4377
//UT-EID=ji4399

public class FairUnifanBathroom { 
	int ticketNum = 0;
	int servingNow = 0;
	int tickets = 4;
	int UTinBathroom = 0;
	int OUinBathroom = 0;
	
    public void enterBathroomUT() throws InterruptedException {
    // Called when a UT fan wants to enter bathroom
    	int myTicket = updateTicketNum() - 1;
    	
    	while (servingNow != myTicket) {
    		Thread.sleep(1);
    	}
    	while (OUinBathroom > 0) {
    		Thread.sleep(1);
    	}
    	while (! tryAcquireTicket())
    		Thread.sleep(1);
    	updateUTinBathroom(1);
    	updateServingNow();
    }
	
	public void enterBathroomOU() throws InterruptedException {
    // Called when a OU fan wants to enter bathroom
		int myTicket = updateTicketNum() - 1;
		
		while(servingNow != myTicket) {
			Thread.sleep(1);
		}
		while (UTinBathroom > 0) {
			Thread.sleep(1);
		}
		while(! tryAcquireTicket())
			Thread.sleep(1);
		updateOUinBathroom(1);
		updateServingNow();
	}
	
	public void leaveBathroomUT() throws InterruptedException {
    // Called when a UT fan wants to leave bathroom
		releaseTicket();
		updateUTinBathroom(-1);
	}

	public void leaveBathroomOU() throws InterruptedException{
    // Called when a OU fan wants to leave bathroom
		releaseTicket();
		updateOUinBathroom(-1);
	}
	
	public synchronized void updateServingNow() {
		++servingNow;
	}
	
	public synchronized int updateTicketNum() {
		++ticketNum;
		return ticketNum;
	}
	
	public synchronized boolean tryAcquireTicket() throws InterruptedException {
		if (tickets == 0) {
			return false;
		}
		tickets -= 1;
		return true;
	}
	
	public synchronized void releaseTicket() throws InterruptedException {
		tickets += 1;
	}
	
	public synchronized void updateUTinBathroom(int i) throws InterruptedException {
		UTinBathroom += i;
	}
	
	public synchronized void updateOUinBathroom(int i) throws InterruptedException {
		OUinBathroom += i;
	}
}
