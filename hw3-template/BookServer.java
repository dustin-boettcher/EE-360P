import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Scanner;

public class BookServer {
	static int tcpPort;
    static int udpPort;
    static int recordId;
    static Hashtable<String, Integer> inventory;
    static Hashtable<Integer, String[]> borrows;
    
  public static void main (String[] args) {
	if (args.length != 1) {
	    System.out.println("ERROR: Provide 1 argument: input file containing initial inventory");
	    System.exit(-1);
    }
	String fileName = args[0]; 
	inventory = new Hashtable<String, Integer>();
    borrows = new Hashtable<Integer, String[]>();
    tcpPort = 7000;
    udpPort = 8000;
    recordId = 1;

    // parse the inventory file
    try {
        Scanner sc = new Scanner(new FileReader(fileName));
          while(sc.hasNextLine()) {
          String book = sc.nextLine();
          String[] tokens = book.split(" ");
          inventory.put(tokens[0], Integer.parseInt(tokens[1]));
        }
        sc.close();
    } catch (FileNotFoundException e) {
	e.printStackTrace();
    }

    //handle request from clients
    try{
    	ServerSocket listener = new ServerSocket(7000);
    	Socket s;
    	while((s = listener.accept()) != null){
    		Thread t = new ServerThread(inventory, s);
    		t.start();
    	}
    	listener.close();
    } catch(IOException e){System.err.println("Server aborted :" + e);}
  }
  
  //Class ServerThread
  //Handles one Client
  //Receives commands and sends replies to Client
  static class ServerThread extends Thread implements Runnable {
	  Socket s;
	  Hashtable<String, Integer> inventory;
	  
    public ServerThread(Hashtable<String, Integer> inventory, Socket s) {
    	this.inventory = inventory;
    	this.s = s;
    }
    
	@Override
	public void run() {
		try {
			Scanner sc = new Scanner(s.getInputStream());
			PrintWriter pout = new PrintWriter(s.getOutputStream());
			String command = sc.nextLine();
			while (command != null) {
				String[] tokens = command.split(" ");
				if (tokens[0].equals("return"))
					pout.print(returnBook(Integer.parseInt(tokens[1])));
				if (tokens[0].equals("borrow"))
					pout.print(borrowBook(tokens[1], tokens[2]));
			}
			sc.close();
		} catch(IOException e) {}
		
	}
	  
  }
  
  //Synchronized method to return a Book
  //returns message for client
  static synchronized String returnBook(int recordId) {
	  if(borrows.containsKey(recordId)) {
		  String bookName = borrows.get(recordId)[1];
		  inventory.put(bookName, inventory.get(bookName) + 1);
		  return recordId + " is returned";
	  }
	  return recordId + " not found, no such borrow record";
  }
  
  //Synchronized method to borrow a Book
  //returns message for client
  static synchronized String borrowBook(String student, String book) {
	  if (inventory.containsKey(book)) {
		  if (inventory.get(book) > 0) {
			  String[] record = {student, book};
			  borrows.put(recordId, record);
			  inventory.put(book, inventory.get(book) - 1);
			  ++recordId;
			  return "Your request has been approved, " + (recordId - 1) + " " + student + " " + book;
		  }
		  return "Request failed - Book not available";
	  }
	  return "Request failed - We do not have this book";
  }
}