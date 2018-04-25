import java.io.*;
import java.io.FileOutputStream;
import java.net.*;
import java.nio.*;

/*

Name: HANSEL CHIA
Student number: A0170121B
Is this a group submission (yes/no)? NO

If it is a group submission:
Name of 2nd group member: THE_OTHER_NAME_HERE_PLEASE
Student number of 2nd group member: THE_OTHER_NO

*/


class Bob {
    private int seqNum = 0;
    private DatagramSocket socket;

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
        DatagramPacket fileNameData = new DatagramPacket(byteBuffer,
          byteBuffer.length);
        String fileName;

        socket = new DatagramSocket(port);
        socket.receive(fileNameData);
        fileName = new String(fileNameData.getData(), 0,
          fileNameData.getLength() - 0);

        FileOutputStream fos = new FileOutputStream(fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
    }
}
