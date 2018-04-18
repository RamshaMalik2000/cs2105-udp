/*

Name: HANSEL CHIA
Student number: A0170121B
Is this a group submission (yes/no)? NO

If it is a group submission:
Name of 2nd group member: THE_OTHER_NAME_HERE_PLEASE
Student number of 2nd group member: THE_OTHER_NO

*/

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.*;
import java.util.zip.CRC32;

// This class is not absolutely necessary as you can mash everything
// directly into Alice and Bob classes. However, it might be nicer
// to have this class, which makes your code more organized and readable.
// Furthermore, it makes the assignment easier, as you might be able to
// reuse code

class Packet {
    // Implement me - data, crc, seqNum, type of packet
    private byte[] data;
    private int sequenceNumber;
    private String fileName;
    public final static int CHECKSUM_SIZE = 8;
    public final static int SEQNUM_SIZE = 4;
    public final static int ACK_SIZE = 4;
    public Type type;
    public enum Type {
      ACK, DATA, FILENAME, CORRUPT
    }

    // Constructor
    public Packet() {}
    public Packet(byte[] data, Type type, int sequenceNumber)
      throws Exception
    {
      this.data = data;
      this.type = type;
      if(this.type == Type.ACK) {
        // do nothing - no sequenceNumber
      } else if(this.type == Type.DATA) {
        // get sequenceNumber for data sequence, case of lost/corrupted packets
        this.sequenceNumber = sequenceNumber;
      } else if(this.type == Type.FILENAME) {
        // computeFileName
        this.fileName = computeFileName(data);
      }
    }

    // Static FileName converter
    public String computeFileName(byte[] data)
      throws Exception {
      return new String(data, "UTF-8");
    }

    // Static CRC checker

    // Get file name
    public String getFileName() {
      return fileName;
    }

    // Get sequence number
    public int getSequenceNumber() {
      return sequenceNumber;
    }
    // Get data
    public byte[] getData() {
      return data;
    }

    public boolean isType(Type type) {
      return (this.type == type);
    }

    public Packet parsePacket(byte[] data) {
      // Run CRC checker
      // if not corrupt
        // determine type of packet by byte[] length
      // else
        // assign CORRUPT type
      return new Packet();
    }
}
