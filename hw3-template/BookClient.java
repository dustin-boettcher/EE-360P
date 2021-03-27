import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.io.*;
public class BookClient {
	static int tcpPort;
    static int udpPort;
    static int clientId;
    static boolean firstLine;
    
  public static void main (String[] args) {
    String hostAddress;
    Scanner din;
    PrintStream pout;
    Socket tcpSocket;

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
    firstLine = true;

    boolean isTCP = false;

    try {
      tcpSocket = new Socket(hostAddress, tcpPort);
      din = new Scanner(tcpSocket.getInputStream());
      pout = new PrintStream(tcpSocket.getOutputStream());

      InetAddress ia = InetAddress.getByName(hostAddress);
      DatagramSocket udpSocket = new DatagramSocket();
      byte[] rbuffer = new byte[4096];
      DatagramPacket sPacket, rPacket;

      Scanner sc = new Scanner(new FileReader(commandFile));

      String outputFileName = "out_" + clientId + ".txt";
      FileWriter outputWriter = new FileWriter(outputFileName);

      while(sc.hasNextLine()) {
        String cmd = sc.nextLine();
        String[] tokens = cmd.split(" ");
        String command = "";
        for (int i = 0; i < tokens.length; i++) {
          command += tokens[i];
          if (i != tokens.length - 1) command += " ";
        }

        if (tokens[0].equals("setmode")) {
          if (tokens[1].equals("T")) {
            isTCP = true;
            writeToFile(outputWriter, "The communication mode is set to TCP");
          } else if (tokens[1].equals("U")) {
            isTCP = false;
            writeToFile(outputWriter, "The communication mode is set to UDP");
          } else {
            System.out.println("ERROR: No such command");
          }
        }
        else if (tokens[0].equals("borrow")) {
          String result;
          if (isTCP) {
        	  
            pout.println(command);
            pout.flush();
            result = din.nextLine();
          } else {
            byte[] buffer = new byte[command.length()];
            buffer = command.getBytes();
            sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
            udpSocket.send(sPacket);
            rPacket = new DatagramPacket(rbuffer, rbuffer.length);
            udpSocket.receive(rPacket);
            result = new String(rPacket.getData(), 0, rPacket.getLength());
          }
          writeToFile(outputWriter, result);
        } else if (tokens[0].equals("return")) {
          String result;
          if (isTCP) {
            pout.println(command);
            pout.flush();
            result = din.nextLine();
          } else {
            byte[] buffer = new byte[command.length()];
            buffer = command.getBytes();
            sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
            udpSocket.send(sPacket);
            rPacket = new DatagramPacket(rbuffer, rbuffer.length);
            udpSocket.receive(rPacket);
            result = new String(rPacket.getData(), 0, rPacket.getLength());
          }
          writeToFile(outputWriter, result);
        } else if (tokens[0].equals("inventory")) {
          if (isTCP) {
            pout.println(command);
            pout.flush();
            String length = din.nextLine();
            String[] result = new String[Integer.parseInt(length)];
            for (int i = 0; i < Integer.parseInt(length); i++) {
              result[i] = din.nextLine();
            }
            for (int i = 0; i < Integer.parseInt(length); i++) {
              writeToFile(outputWriter, result[i]);
            }
          } else {
            String[] result;
            byte[] buffer = new byte[command.length()];
            buffer = command.getBytes();
            sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
            udpSocket.send(sPacket);
            rPacket = new DatagramPacket(rbuffer, rbuffer.length);
            udpSocket.receive(rPacket);
            String length = new String(rPacket.getData(), 0, rPacket.getLength());
            result = new String[Integer.parseInt(length.substring(0, 1))];
            for (int i = 0; i < Integer.parseInt(length.substring(0, 1)); i++) {
            	rPacket = new DatagramPacket(rbuffer, rbuffer.length);
            	udpSocket.receive(rPacket);
            	result[i] = new String(rPacket.getData(), 0, rPacket.getLength());
            }
            for (int i = 0; i < Integer.parseInt(length.substring(0, 1)); i++) {
            	writeToFile(outputWriter, result[i]);
            }
          }
        } else if (tokens[0].equals("list")) {
          String[] result;
          if (isTCP) {
        	  pout.println(command);
              pout.flush();
              String length = din.nextLine();
              result = new String[Integer.parseInt(length)];
              for (int i = 0; i < Integer.parseInt(length); i++) {
                result[i] = din.nextLine();
              }
              for (int i = 0; i < Integer.parseInt(length); i++) {
                writeToFile(outputWriter, result[i]);
              }
          } else {
            byte[] buffer = new byte[command.length()];
            buffer = command.getBytes();
            sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
            udpSocket.send(sPacket);
            rPacket = new DatagramPacket(rbuffer, rbuffer.length);
            udpSocket.receive(rPacket);
            String length = new String(rPacket.getData(), 0, rPacket.getLength());
            result = new String[Integer.parseInt(length.substring(0, 1))];
            for (int i = 0; i < Integer.parseInt(length.substring(0, 1)); i++) {
            	rPacket = new DatagramPacket(rbuffer, rbuffer.length);
            	udpSocket.receive(rPacket);
            	result[i] = new String(rPacket.getData(), 0, rPacket.getLength());
            }
            for (int i = 0; i < Integer.parseInt(length.substring(0, 1)); i++) {
            	writeToFile(outputWriter, result[i]);
            }
          }
          //outputWriter.write(result + "\n");
        } else if (tokens[0].equals("exit")) {
          if (isTCP) {
            pout.println(command);
            pout.flush();
          } else {
            byte[] buffer = new byte[command.length()];
            buffer = command.getBytes();
            sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
            udpSocket.send(sPacket);
          }
          tcpSocket.close();
          udpSocket.close();
          outputWriter.close();
        } else {
          System.out.println("ERROR: No such command");
        }
      }
      sc.close();
    } catch (IOException e) {
	  e.printStackTrace();
    }
    
  }
  
  static void writeToFile(FileWriter writer, String str) throws IOException {
	  if (! firstLine)
		  writer.write("\n");
	  else firstLine = false;
	  if (str.charAt(str.length() - 1) == '\n')
		  str = str.substring(0, str.length() - 1);
	  writer.write(str);
  }
}