/*

Name: HANSEL CHIA
Student number: A0170121B
Is this a group submission (yes/no)? NO

If it is a group submission:
Name of 2nd group member: THE_OTHER_NAME_HERE_PLEASE
Student number of 2nd group member: THE_OTHER_NO

*/


// Please DO NOT copy from the Internet (or anywhere else)
// Instead, if you see nice code somewhere try to understand it.
//
// After understanding the code, put it away, do not look at it,
// and write your own code.
// Subsequent exercises will build on the knowledge that
// you gain during this exercise. Possibly also the exam.
//
// We will check for plagiarism. Please be extra careful and
// do not share solutions with your friends.
//
// Good practices include
// (1) Discussion of general approaches to solve the problem
//     excluding detailed design discussions and code reviews.
// (2) Hints about which classes to use
// (3) High level UML diagrams
//
// Bad practices include (but are not limited to)
// (1) Passing your solution to your friends
// (2) Uploading your solution to the Internet including
//     public repositories
// (3) Passing almost complete skeleton codes to your friends
// (4) Coding the solution for your friend
// (5) Sharing the screen with a friend during coding
// (6) Sharing notes
//
// If you want to solve this assignment in a group,
// you are free to do so, but declare it as group work above.

import java.io.*;
import java.io.IOException;
import java.net.*;
import java.net.InetAddress;
import java.nio.*;
import java.util.Arrays;
import java.util.Scanner;

class Alice {
    private int seqNum = 0;
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private final int BUFFER_SIZE = 1010;

    public static void main(String[] args) throws Exception {
        // Do not modify this method
        if (args.length != 3) {
          System.out.println("Usage: java Alice <path/filename> <unreliNetPort>"
             + " <rcvFileName>");
            System.exit(1);
        }
        new Alice(args[0], Integer.parseInt(args[1]), args[2]);
    }

    public Alice(String fileToSend, int port, String filenameAtBob) throws
    Exception
    {
      // Implement me
      // create socket with port
      socket = new DatagramSocket();
      this.port = port;
      address = InetAddress.getByName("localhost");
      // sendFileName - take Strin filenameAtBob
      if(sendFileName(filenameAtBob))
      // sendFile - take fileToSend
        System.out.println("Alc/ Sending file!!");
        sendFile(fileToSend);
    }

    private boolean sendFileName(String fileName)
      throws Exception, IOException {
      System.out.println("Sending: " + fileName + "...");
      Packet p = new Packet(fileName.getBytes(), Packet.Type.FILENAME, 0);
      byte[] toSend = Packet.toBytes(p);
      DatagramPacket dp = new DatagramPacket(toSend, 0,
        toSend.length, address, port);
      socket.send(dp);
      return true;
    }

    private void sendFile(String fileName) {
      try {
        DatagramPacket dp;
        FileInputStream fis = new FileInputStream(fileName);
        BufferedInputStream bis = new BufferedInputStream(fis);

        int len = bis.available();
        byte[] sendData = new byte[BUFFER_SIZE];

        while(len > 0) {
          System.out.println("Alc/ File sending");
          if(len >= BUFFER_SIZE) {
            len -= BUFFER_SIZE;
            sendData = new byte[BUFFER_SIZE];
            bis.read(sendData, 0, BUFFER_SIZE);
            Packet p = new Packet(sendData, Packet.Type.DATA, seqNum);
            dp = new DatagramPacket(Packet.toBytes(p), 0, BUFFER_SIZE + 14, address, port);
          } else {
            sendData = new byte[len];
            bis.read(sendData, 0, len);
            Packet p = new Packet(sendData, Packet.Type.DATA, seqNum);
            dp = new DatagramPacket(Packet.toBytes(p), 0, len + 14, address, port);
            len = 0;
          }
          System.out.println("Alc/ File sent, len: " + len);
          sendPacket(dp);
        }

        Packet p = new Packet(new byte[1], Packet.Type.ENDOFFILE, seqNum + 1);
        dp = new DatagramPacket(Packet.toBytes(p), 0, 11, address, port);
        sendPacket(dp);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    private void sendPacket(DatagramPacket packet) {
      try {
        socket.send(packet);
        while(true) {
          int ackNum = waitAck(socket);
          System.out.println("Alc/ sendPkt ACK: " + ackNum);
    			if(ackNum == seqNum) {
    				socket.send(packet);
    			} else {
    				seqNum = ackNum;
    				break;
    			}
        }
      } catch (Exception e) {
        System.out.println("Alc/ ERROR sendPkt: " + e.getMessage());
        sendPacket(packet);
      }
    }

    public int waitAck(DatagramSocket socket) throws Exception {
      System.out.print("Alc/ ACK Waiting");
  		while(true) {
        System.out.println(" + Looping on address: " + address + ", port: " + port);
  			byte[] buffer = new byte[Packet.SEQNUM_SIZE + Packet.CHECKSUM_SIZE];
  			DatagramPacket receivedPkt = new DatagramPacket(buffer, 0, buffer.length, address, port);
  			socket.setSoTimeout(1000);
        socket.receive(receivedPkt);
  			if(receivedPkt != null) {
  				if(Packet.validChecksum(buffer)) {
            int ackNum = Packet.ByteConversionUtil.byteArrayToInt(Arrays.copyOfRange(buffer, 10, 14));
            System.out.println("Alc/ Received ACK: " + ackNum);
  					return ackNum;
  				}
  			}
  		}
    }
}
