/*

Name: HANSEL CHIA
Student number: A0170121B
Is this a group submission (yes/no)? NO

If it is a group submission:
Name of 2nd group member: THE_OTHER_NAME_HERE_PLEASE
Student number of 2nd group member: THE_OTHER_NO

*/

import java.io.*;
import java.io.FileOutputStream;
import java.net.*;
import java.nio.*;
import java.util.Arrays;

class Bob {
    private int seqNum = 0;
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;

    public static void main(String[] args) throws Exception {
        // Do not modify this method
        if (args.length != 1) {
            System.out.println("Usage: java Bob <port>");
            System.exit(1);
        }
        new Bob(Integer.parseInt(args[0]));
    }

    public Bob(int port) throws Exception {
        // Implement me
        byte[] byteBuffer = new byte[1024];
        byte[] fileBuffer = null;
        DatagramPacket fileNameData = new DatagramPacket(byteBuffer,
          byteBuffer.length);
        DatagramPacket fileData = new DatagramPacket(byteBuffer,
          byteBuffer.length);
        String fileName;
        boolean isRunning = true;

        socket = new DatagramSocket(port);
        socket.receive(fileNameData);
        fileBuffer = Arrays.copyOfRange(fileNameData.getData(), 0, fileNameData.getLength());
        Packet.Type packetType = Packet.computeType(fileBuffer);
        if(packetType == Packet.Type.ACK)
          System.out.println("ACK received");
        else
        if(packetType == Packet.Type.DATA)
          System.out.println("DATA received");
        else
        if(packetType == Packet.Type.FILENAME)
          System.out.println("FILENAME received");
        else
        if(packetType == Packet.Type.ENDOFFILE)
          System.out.println("ENDOFFILE received");
        else
          System.out.println("Other type received");
        fileName = (Packet.parsePacket(fileBuffer)).getFileName();
        System.out.println("Bob/ FILENAME = " + fileName);

        FileOutputStream fos = new FileOutputStream(fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        while(isRunning) {
          System.out.print("Bob/ File waiting");
          socket.receive(fileData);
          System.out.println(" + received");
          byteBuffer = Arrays.copyOfRange(fileData.getData(), 0, fileData.getLength());
          serverAddress = InetAddress.getByName("localhost"); //
			    serverPort = port;//fileData.getPort(); //
          System.out.println("Bob/ Parsing byte");
          Packet p = Packet.parsePacket(byteBuffer);
          // System.out.println("Bob/ Seq num: " + p.getSequenceNumber());
          switch(p.getType()) {
            case DATA:
              bos.write(p.getData(), 0, p.getData().length);
              if(p.getSequenceNumber() == seqNum) {
					      seqNum = (seqNum + 1) % 2;
				      }
              sendAck(socket);
              // get seqNum, modify ACK and send
              break;
            case ENDOFFILE:
              isRunning = false;
              if(p.getSequenceNumber() == seqNum) {
					      seqNum = (seqNum + 1) % 2;
				      }
              sendAck(socket);
              // modify ACK and send
              break;
            case CORRUPT:
              sendAck(socket);
              // re-send ACK
              break;
            default:
          }
        }
        bos.close();
    }

    public void sendAck(DatagramSocket socket) throws Exception {
    	byte[] ackNum = Packet.ByteConversionUtil.intToByteArray(seqNum);
    	Packet p = new Packet(ackNum, Packet.Type.ACK, 0);
    	DatagramPacket AckPkt = new DatagramPacket(Packet.toBytes(p), Packet.toBytes(p).length, serverAddress, serverPort);
      System.out.println("Bob/ Sending ACK: " + seqNum + " to " + "svrAdr: " + serverAddress + ", svrPort: " + serverPort);
    	socket.send(AckPkt);
    }
}
