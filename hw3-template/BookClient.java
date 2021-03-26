import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.*;
import java.util.*;
public class BookClient {
  public static void main (String[] args) {
    String hostAddress;
    int tcpPort;
    int udpPort;
    int clientId;

    Scanner din;
    PrintStream pout;
    Socket server;

    if (args.length != 2) {
      System.out.println("ERROR: Provide 2 arguments: commandFile, clientId");
      System.out.println("\t(1) <command-file>: file with commands to the server");
      System.out.println("\t(2) client id: an integer between 1..9");
      System.exit(-1);
    }

    String commandFile = args[0];
    clientId = Integer.parseInt(args[1]);
    hostAddress = "localhost";
    tcpPort = 7000;// hardcoded -- must match the server's tcp port
    udpPort = 8000;// hardcoded -- must match the server's udp port

    try {
      server = new Socket(hostAddress, tcpPort);
      din = new Scanner(server.getInputStream());
      pout = new PrintStream(server.getOutputStream());

      Scanner sc = new Scanner(new FileReader(commandFile));

      while(sc.hasNextLine()) {
        String cmd = sc.nextLine();
        String[] tokens = cmd.split(" ");

        if (tokens[0].equals("setmode")) {
          // TODO: set the mode of communication for sending commands to the server
          if (tokens[1].equals("T")) {
            if (!server.isClosed()) {
              server.close();
            }
            server = new Socket(hostAddress, tcpPort);
            din = new Scanner(server.getInputStream());
            pout = new PrintStream(server.getOutputStream());
            System.out.println("The communication mode is set to TCP");
          } else if (tokens[1].equals("U")) {
            if (!server.isClosed()) {
              server.close();
            }
            server = new Socket(hostAddress, udpPort);
            din = new Scanner(server.getInputStream());
            pout = new PrintStream(server.getOutputStream());
            System.out.println("The communication mode is set to UDP");
          } else {
            System.out.println("ERROR: No such command");
          }
        }
        else if (tokens[0].equals("borrow")) {
          // TODO: send appropriate command to the server and display the
          // appropriate responses form the server
          String command = "";
          for (int i = 0; i < tokens.length; i++) {
            command += tokens[i];
            if (i != tokens.length - 1) command += " ";
          }
          pout.println(command);
          pout.flush();
          String result = din.nextLine();
          System.out.println(result);
        } else if (tokens[0].equals("return")) {
          // TODO: send appropriate command to the server and display the
          // appropriate responses form the server
          pout.println(tokens[0] + " " + tokens[1]);
          pout.flush();
          String result = din.nextLine();
          System.out.println(result);
        } else if (tokens[0].equals("inventory")) {
          // TODO: send appropriate command to the server and display the
          // appropriate responses form the server
          pout.println(tokens[0]);
          pout.flush();
          String result = din.nextLine();
          System.out.println(result);
        } else if (tokens[0].equals("list")) {
          // TODO: send appropriate command to the server and display the
          // appropriate responses form the server
          pout.println(tokens[0] + " " + tokens[1]);
          pout.flush();
          String result = din.nextLine();
          System.out.println(result);
        } else if (tokens[0].equals("exit")) {
          // TODO: send appropriate command to the server
          pout.println(tokens[0]);
          pout.flush();
          String result = din.nextLine();
          System.out.println(result);
        } else {
          System.out.println("ERROR: No such command");
        }
      }
    } catch (IOException e) {
	  e.printStackTrace();
    }
  }
}
