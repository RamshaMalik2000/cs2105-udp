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
import java.util.Arrays;
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
    private Type type;
    private short typeNumber;
    public final static int CHECKSUM_SIZE = 8;
    public final static int TYPE_SIZE = 2;
    public final static int SEQNUM_SIZE = 4;
    public final static int ACK_SIZE = 4;
    public enum Type {
      ACK, DATA, FILENAME, ENDOFFILE, CORRUPT
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
    public static boolean validChecksum(byte[] data) {
      CRC32 checksum = new CRC32();
  		checksum.update(data, 8, data.length - 8);
  		long checksumVal = checksum.getValue();
  		long oldCheckSum = ByteConversionUtil.byteArrayToLong(
        Arrays.copyOfRange(data, 0, 8));
  		if(checksumVal == oldCheckSum) {
  			return true;
  		} else {
  			return false;
  		}
    }

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

    public Packet parsePacket(byte[] data) throws Exception {
      // Run CRC checker - if not corrupt, remove CRC header
      if(validChecksum(data)) {

        // determine type of packet by type[2 bits]
        // packet - DATA
          // [crc[8]] [type[2] - 0] [seq[4]] [data[1012]]
        // packet - FILENAME
          // [crc[8]] [type[2] - 1] [data[numberOfCharacters]]
        // packet - ACK
          // [crc[8]] [type[2] - 2] [data[4]]
        return new Packet(); // implement
      } else {
        return new Packet(new byte[1], Type.CORRUPT, 0);
      }
    }

    public byte[] toBytes(Packet p) {
        // depending on type
        // append CRC to front
        if(this.isType(Type.ACK)) {
          // crc8 type2 data4
        } else
        if(this.isType(Type.FILENAME)) {
          // crc8 type2 data_n
        } else
        if(this.isType(Type.DATA)) {
          // crc8 type2 seq4 data1012
        }
        if(this.isType(Type.ENDOFFILE)) {

        }
        return new byte[1]; // implement
    }

    public static class ByteConversionUtil {
        private static ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);
  	    private static ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
        private static ByteBuffer shortBuffer = ByteBuffer.allocate(Short.BYTES);

  		  public static byte[] longToByteArray(long value) {
            longBuffer.putLong(0, value);
            return longBuffer.array();
  		   }

  		  public static long byteArrayToLong(byte[] array) {
            longBuffer = ByteBuffer.allocate(Long.BYTES);
            longBuffer.put(array, 0, array.length);
            longBuffer.flip();
            return longBuffer.getLong();
  		  }

    		public static int byteArrayToInt(byte[] array) {
            intBuffer = ByteBuffer.allocate(Integer.BYTES);
      			intBuffer.put(array, 0, array.length);
    			  intBuffer.flip();
            return intBuffer.getInt();
    		}

        public static byte[] intToByteArray(int value) {
            intBuffer.putInt(0, value);
            return intBuffer.array();
    		}

        public static short byteArrayToShort(byte[] array) {
          shortBuffer = ByteBuffer.allocate(Short.BYTES);
          shortBuffer.put(array, 0, array.length);
          shortBuffer.flip();
          return shortBuffer.getShort();
        }

        public static byte[] shortToByteArray(short value) {
          shortBuffer.putShort(0, value);
          return shortBuffer.array();
        }
    }
}
