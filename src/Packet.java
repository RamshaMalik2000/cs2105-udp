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
import java.util.EnumSet;
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
      switch(type) {
        case ACK:
          // do nothing - no sequenceNumber
          break;
        case DATA:
          // get sequenceNumber for data sequence, case of lost/corrupted packets
          this.sequenceNumber = sequenceNumber;
          System.out.println("Pkt/ DATA seqNum: " + sequenceNumber);
          break;
        case FILENAME:
          // computeFileName
          this.fileName = computeFileName(data);
          break;
        case ENDOFFILE:
          // do nothing
          break;
        case CORRUPT:
          break;
        default:
          break;
      }
    }

    // Static FileName converter
    public String computeFileName(byte[] data)
      throws Exception {
      return new String(data, "UTF-8");
    }

    // Static CRC checker
    public static boolean validChecksum(byte[] sendData) {
      CRC32 checksum = new CRC32();
  		checksum.update(sendData, CHECKSUM_SIZE, sendData.length - CHECKSUM_SIZE);
  		long checksumVal = checksum.getValue();
  		long oldCheckSum = ByteConversionUtil.byteArrayToLong(
        Arrays.copyOfRange(sendData, 0, 8));
  		if(checksumVal == oldCheckSum) {
  			return true;
  		} else {
  			return false;
  		}
    }

    private static byte[] removeChecksum(byte[] sendData) {
      return Arrays.copyOfRange(sendData, CHECKSUM_SIZE, sendData.length);
    }

    private static byte[] removeType(byte[] sendData) {
      return Arrays.copyOfRange(sendData, TYPE_SIZE, sendData.length);
    }

    private static byte[] removeSeqNum(byte[] sendData) {
      return Arrays.copyOfRange(sendData, SEQNUM_SIZE, sendData.length);
    }

    // Compute type
    public static Type computeType(byte[] sendData) {
      short typeNumber = ByteConversionUtil.byteArrayToShort(
        Arrays.copyOfRange(sendData, CHECKSUM_SIZE, CHECKSUM_SIZE+TYPE_SIZE));
        int counter = 0;
        Type result = null;
        for(Packet.Type t : Packet.Type.values()) {
          if(counter == (short) typeNumber) {
            result = t;
          }
          counter++;
        }
      return result;
    }

    private static int computeSeqNum(byte[] sendData) {
      int FIRST_BYTE = CHECKSUM_SIZE + TYPE_SIZE;
      return ByteConversionUtil.byteArrayToInt(Arrays.copyOfRange(
        sendData, FIRST_BYTE, FIRST_BYTE + SEQNUM_SIZE));
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

    // Get type
    public Type getType() {
      return type;
    }

    public boolean isType(Type type) {
      return (this.type == type);
    }

    public static Packet parsePacket(byte[] data) throws Exception {
      // Run CRC checker - if not corrupt, remove CRC header
      if(validChecksum(data)) {
        System.out.println("Pkt/ parsePacket data len: " + data.length);
        // determine type of packet by type[2 bits]
        Type packetType = computeType(data);
        switch (packetType) {
          case ACK:
            data = removeChecksum(data);
            data = removeType(data);
            return new Packet(data, Type.ACK, 0);
          case DATA:
            int seqNum = computeSeqNum(data);
            data = removeChecksum(data);
            data = removeType(data);
            data = removeSeqNum(data);
            return new Packet(data, Type.DATA, seqNum);
          // packet - DATA
            // [crc[8]] [type[2] - 0] [seq[4]] [data[1012]]
          // packet - FILENAME
          case FILENAME:
            data = removeChecksum(data);
            data = removeType(data);
            return new Packet(data, Type.FILENAME, 0);
            // [crc[8]] [type[2] - 1] [data[numberOfCharacters]]
          // packet - ACK
            // [crc[8]] [type[2] - 2] [data[4]]
          case ENDOFFILE:
            data = removeChecksum(data);
            System.out.println("Pkt/ ENDOFFILE detected: " + data.length);
            return new Packet(data, Type.ENDOFFILE, 0);
          default:
            break;
        }
        return null;
      } else {
        System.out.println("Pkt/ Corrupt packet detected: " + data.length);
        return new Packet(new byte[1], Type.CORRUPT, 0);
      }
    }

    private static byte[] addCheckSum(byte[] sendData) {
  		CRC32 checksum = new CRC32();
  		checksum.update(sendData);
  		long checksumValue = checksum.getValue();
  		byte[] checksumByteArray = ByteConversionUtil.longToByteArray(checksumValue);
  		sendData = concatenate(checksumByteArray, sendData);
      return sendData;
    }

    private static byte[] addType(byte[] sendData, Type t) {
      short typeValue = -1;
      if(t == Type.ACK)
        typeValue = 0;
      else
      if(t == Type.DATA)
        typeValue = 1;
      else
      if(t == Type.FILENAME)
        typeValue = 2;
      else
      if(t == Type.ENDOFFILE)
        typeValue = 3;
      byte[] typeBytes = ByteConversionUtil.shortToByteArray(typeValue);
      sendData = concatenate(typeBytes, sendData);
      return sendData;
    }

    private static byte[] addSequenceNumber(byte[] sendData, int index) {
 	   	byte[] sequenceNum = ByteConversionUtil.intToByteArray(index);
 		  sendData = concatenate(sequenceNum, sendData);
      return sendData;
    }

    public static byte[] toBytes(Packet p) {
      byte[] sendData = p.getData();
      // depending on type
      Type packetType = p.getType();
      switch(packetType) {
        case ACK:
          // crc8 type2 data4
          sendData = addType(sendData, Type.ACK);
          break;
        case DATA:
          // crc8 type2 seq4 data1012
          sendData = addSequenceNumber(sendData, p.getSequenceNumber());
          sendData = addType(sendData, Type.DATA);
          break;
        case FILENAME:
          // crc8 type2 data
          sendData = addType(sendData, Type.FILENAME);
          break;
        case ENDOFFILE:
          sendData = addType(sendData, Type.ENDOFFILE);
          break;
        default:
      }
      // append CRC to front
      sendData = addCheckSum(sendData);
      System.out.println("Pkt/ To bytes, data len: " + sendData.length);
      return sendData;
    }

    private static byte[] concatenate(byte[] buffer1, byte[] buffer2) {
        byte[] returnBuffer = new byte[buffer1.length + buffer2.length];
        System.arraycopy(buffer1, 0, returnBuffer, 0, buffer1.length);
        System.arraycopy(buffer2, 0, returnBuffer, buffer1.length, buffer2.length);
        return returnBuffer;
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
