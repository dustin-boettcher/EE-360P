import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
            outputWriter.write("The communication mode is set to TCP\n");
          } else if (tokens[1].equals("U")) {
            isTCP = false;
            outputWriter.write("The communication mode is set to UDP\n");
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
            sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
            udpSocket.send(sPacket);
            rPacket = new DatagramPacket(rbuffer, rbuffer.length);
            udpSocket.receive(rPacket);
            result = new String(rPacket.getData(), 0, rPacket.getLength());
          }
          outputWriter.write(result + "\n");
        } else if (tokens[0].equals("return")) {
          String result;
          if (isTCP) {
            pout.println(command);
            pout.flush();
            result = din.nextLine();
          } else {
            byte[] buffer = new byte[command.length()];
            sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
            udpSocket.send(sPacket);
            rPacket = new DatagramPacket(rbuffer, rbuffer.length);
            udpSocket.receive(rPacket);
            result = new String(rPacket.getData(), 0, rPacket.getLength());
          }
          outputWriter.write(result + "\n");
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
              outputWriter.write(result[i] + "\n");
            }
          } else {
            String result;
            byte[] buffer = new byte[command.length()];
            sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
            udpSocket.send(sPacket);
            rPacket = new DatagramPacket(rbuffer, rbuffer.length);
            udpSocket.receive(rPacket);
            result = new String(rPacket.getData(), 0, rPacket.getLength());
            outputWriter.write(result + "\n");
          }
        } else if (tokens[0].equals("list")) {
          String result;
          if (isTCP) {
            pout.println(command);
            pout.flush();
            result = din.nextLine();
          } else {
            byte[] buffer = new byte[command.length()];
            sPacket = new DatagramPacket(buffer, buffer.length, ia, udpPort);
            udpSocket.send(sPacket);
            rPacket = new DatagramPacket(rbuffer, rbuffer.length);
            udpSocket.receive(rPacket);
            result = new String(rPacket.getData(), 0, rPacket.getLength());
          }
          outputWriter.write(result + "\n");
        } else if (tokens[0].equals("exit")) {
          if (isTCP) {
            pout.println(command);
            pout.flush();
          } else {
            byte[] buffer = new byte[command.length()];
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
    } catch (IOException e) {
	  e.printStackTrace();
    }
  }

//  static void sendMessage(String command, boolean isTCP) {
//    if (isTCP) {
//      pout.println(command);
//      pout.flush();
//    }
//  }
}
