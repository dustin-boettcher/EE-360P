import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

public class BookServer {
	static int tcpPort = 7000; //hardcoded
    static int udpPort = 8000; //hardcoded
    static int recordId; //current borrow transaction
    static Hashtable<String, Integer> inventory; //current inventory
    static Hashtable<Integer, String[]> borrows; //list of borrow transactions
    static Hashtable<String, ArrayList<Integer>> studentLists; //each students borrowed books
    static ArrayList<String> books; //list of books for ordering inventory
    
  public static void main (String[] args) {
	if (args.length != 1) {
	    System.out.println("ERROR: Provide 1 argument: input file containing initial inventory");
	    System.exit(-1);
    }
	String fileName = args[0]; 
	inventory = new Hashtable<String, Integer>();
    borrows = new Hashtable<Integer, String[]>();
    studentLists = new Hashtable<String, ArrayList<Integer>>();
    books = new ArrayList<String>();
    recordId = 1;

    // parse the inventory file
    try {
        Scanner sc = new Scanner(new FileReader(fileName));
          while(sc.hasNextLine()) {
          String book = sc.nextLine();
          String[] tokens = parseString(book);
          inventory.put(tokens[0], Integer.parseInt(tokens[1]));
          books.add(tokens[0]);
        }
        sc.close();
        
    } catch (FileNotFoundException e) {
	e.printStackTrace();
    }

    //handle request from clients
    try{
    	ServerSocket listener = new ServerSocket(7000);
    	Socket s;
    	
    	//DEBUGGING
    	Thread a = new ServerThread();
    	a.start();
    	//
    	
    	while((s = listener.accept()) != null){
    		Thread t = new ServerThread();
    		t.start();
    	}
    	listener.close();
    } catch(IOException e){System.err.println("Server aborted :" + e);}
  }
  
  //Class ServerThread
  //Handles one Client
  //Receives commands and sends replies to Client
  static class ServerThread extends Thread implements Runnable {
	  Socket s; //for TCP
	  Scanner sc; //for TCP
	  PrintWriter pout; //for TCP
	  boolean isUDP;
	  
	  //DEBUGGING: input from console
	  BufferedReader reader = new BufferedReader(
	            new InputStreamReader(System.in));
	  
    public ServerThread() {
    	this.isUDP = true;
    }
    
	@Override
	public void run() {
		try {
			String command = receiveCommand();
			
			//loop to handle commands
			while (command != null) {
				String[] tokens = parseString(command);
				if (tokens[0].equals("setmode")) {
					sendMessage(setMode(tokens[1]));
				}
				if (tokens[0].equals("return"))
					sendMessage(returnBook(Integer.parseInt(tokens[1])) + "\n");
				if (tokens[0].equals("borrow")) {
					sendMessage(borrowBook(tokens[1], tokens[2]) + "\n");
				}
				if (tokens[0].equals("list")) {
					ArrayList<String> list = listBooks(tokens[1]);
					for (String str: list)
						sendMessage(str + "\n");
				}
				if (tokens[0].equals("inventory")) {
					ArrayList<String> list = listInventory();
					for (String str: list)
						sendMessage(str + "\n");
				}
				if (tokens[0].equals("exit")) {
					
					//TODO: close the connection and make inventory file
				}
				command = receiveCommand();
			}
			sc.close(); s.close(); pout.close();
		} catch(IOException e) {}
	}
	
	//send message with TCP or UDP
	void sendMessage(String str) {
		//DEBUGGING
		System.out.print(str);
		//UDP
		
		//TCP
		//if (!isUDP)
			//pout.print(str);
		
	}
	
	//receive message with TCP or UDP
	String receiveCommand() throws IOException {
		return reader.readLine();
	}
	
	//Changes the mode of communication between client and server
	String setMode(String str) throws IOException {
		if (str.equals("T")) {
			if (isUDP) {
				s = new Socket();
				sc = new Scanner(s.getInputStream());
				pout = new PrintWriter(s.getOutputStream());
			}
			return "The communication mode is set to TCP";
		}
		else {
			return "The communication mode is set to UDP";
		}
	}
	  
  }
  
  //Synchronized method to return a Book
  //returns message for client
  static synchronized String returnBook(int recordId) {
	  if(borrows.containsKey(recordId)) {
		  String student = borrows.get(recordId)[0];
		  String bookName = borrows.get(recordId)[1];
		  inventory.put(bookName, inventory.get(bookName) + 1);
		  studentLists.get(student).remove((Integer) recordId);
		  borrows.remove(recordId);
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
			  if (studentLists.containsKey(student)) {
				  studentLists.get(student).add(recordId);
			  }
			  else  {
				  studentLists.put(student, new ArrayList<Integer>());
				  studentLists.get(student).add(recordId);
			  }
			  ++recordId;
			  return "Your request has been approved, " + (recordId - 1) + " " + student + " " + book;
		  }
		  return "Request failed - Book not available";
	  }
	  return "Request failed - We do not have this book";
  }
  
  //lists the books borrowed by a specific student
  static synchronized ArrayList<String> listBooks(String student) {
	  ArrayList<String> list = new ArrayList<String>();
	  if(studentLists.containsKey(student) && (! studentLists.get(student).isEmpty())) {
		  ArrayList<Integer> recordList = studentLists.get(student);
		  for (Integer i: recordList)
			  list.add(i + " " + borrows.get(i)[1]);
	  }
	  else list.add("No record found for " + student);
	  return list;
	  
  }
  
  //lists the current inventory
  static synchronized ArrayList<String> listInventory() {
	  ArrayList<String> list = new ArrayList<String>();
	  for (String str: books)
		  list.add(str + " " + inventory.get(str));
	  return list;
  }
  
  //parses commands with quotation marks for book names
  static String[] parseString(String str) {
	  String[] strings = new String[3];
	  int idx = 0;
	  String[] split = str.split(" ");
	  for (int i = 0; i < split.length; i++) {
		  if (split[i].charAt(0) == '"') {
			  strings[idx] = split[i];
			  while(split[i].charAt(split[i].length() - 1) != '"') {
				  strings[idx] += " " + split[i + 1];
				  ++i;
			  }
		  }
		  else strings[idx] = split[i];
		  ++idx;
	  }
	  return strings;
  } 
}