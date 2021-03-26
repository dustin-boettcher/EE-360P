import java.net.Socket;
import java.util.Scanner;
import java.io.*;
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

      String outputFileName = "out_" + clientId + ".txt";
      FileWriter outputWriter = new FileWriter(outputFileName);

      while(sc.hasNextLine()) {
        String cmd = sc.nextLine();
        String[] tokens = cmd.split(" ");

        if (tokens[0].equals("setmode")) {
          if (tokens[1].equals("T")) {
            server.close();
            server = new Socket(hostAddress, tcpPort);
            din = new Scanner(server.getInputStream());
            pout = new PrintStream(server.getOutputStream());
            outputWriter.write("The communication mode is set to TCP\n");
          } else if (tokens[1].equals("U")) {
            server.close();
            server = new Socket(hostAddress, udpPort);
            din = new Scanner(server.getInputStream());
            pout = new PrintStream(server.getOutputStream());
            outputWriter.write("The communication mode is set to UDP\n");
          } else {
            System.out.println("ERROR: No such command");
          }
        }
        else if (tokens[0].equals("borrow")) {
          String command = "";
          for (int i = 0; i < tokens.length; i++) {
            command += tokens[i];
            if (i != tokens.length - 1) command += " ";
          }
          pout.println(command);
          pout.flush();
          String result = din.nextLine();
          outputWriter.write(result + "\n");
        } else if (tokens[0].equals("return")) {
          pout.println(tokens[0] + " " + tokens[1]);
          pout.flush();
          String result = din.nextLine();
          outputWriter.write(result + "\n");
        } else if (tokens[0].equals("inventory")) {
          pout.println(tokens[0]);
          pout.flush();
          String length = din.nextLine();
          String[] result = new String[Integer.parseInt(length)];
          for (int i = 0; i < Integer.parseInt(length); i++) {
            result[i] = din.nextLine();
          }
          for (int i = 0; i < Integer.parseInt(length); i++) {
            outputWriter.write(result[i] + "\n");
          }
        } else if (tokens[0].equals("list")) {
          pout.println(tokens[0] + " " + tokens[1]);
          pout.flush();
          String result = din.nextLine();
          outputWriter.write(result + "\n");
        } else if (tokens[0].equals("exit")) {
          pout.println(tokens[0]);
          pout.flush();
          server.close();
          outputWriter.close();
        } else {
          System.out.println("ERROR: No such command");
        }
      }
    } catch (IOException e) {
	  e.printStackTrace();
    }
  }
}
