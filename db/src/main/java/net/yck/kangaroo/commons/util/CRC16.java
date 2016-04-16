package net.yck.kangaroo.commons.util;

/**
 * CRC16 implementation according to CCITT standards.
 * 
 * Width = 16 bits
 * 
 * Truncated polynomial = 0x1021
 * 
 * Initial value = 0xFFFF
 * 
 * Input data is NOT reflected
 * 
 * Output CRC is NOT reflected
 * 
 * No XOR is performed on the output CRC
 */
public class CRC16 {

  private static int INITIAL = 0xFFFF;
  private static int MASK = 0xFFFF;
  private static int POLY = 0x1021;

  /**
   * @param buffer
   * @return 16-bit CRC
   * 
   *         http://stackoverflow.com/questions/13209364/convert-c-crc16-to-java-crc16
   */
  public static int fast_crc(final byte[] buffer) {
    int crc = INITIAL;
    for (int j = 0; j < buffer.length; j++) {
      crc = ((crc >>> 8) | (crc << 8)) & MASK;
      crc ^= (buffer[j] & 0xff);// byte to int, trunc sign
      crc ^= ((crc & 0xff) >> 4);
      crc ^= (crc << 12) & 0xffff;
      crc ^= ((crc & 0xFF) << 5) & MASK;
    }
    return crc & MASK;
  }


  /**
   * @param buffer
   * @return 16-bit CRC
   * 
   *         http://srecord.sourceforge.net/crc16-ccitt.html#source
   */
  public static int good_crc(final byte[] buffer) {
    int crc = INITIAL;
    for (int j = 0; j < buffer.length; j++) {
      crc = update_good_crc(crc, buffer[j]);
    }
    crc = augment_message_for_good_crc(crc);
    return crc & MASK;
  }

  private static int update_good_crc(int crc, byte ch) {
    /*
     * Align test bit with leftmost bit of the message byte.
     */
    int v = 0x80;

    for (int i = 0; i < 8; i++) {
      boolean xor_flag = (crc & 0x8000) != 0;

      crc = crc << 1;

      if ((ch & v) != 0) {
        /*
         * Append next bit of message to end of CRC if it is not zero. The zero bit placed there by
         * the shift above need not be changed if the next bit of the message is zero.
         */
        crc = crc + 1;
      }

      if (xor_flag) {
        crc = crc ^ POLY;
      }

      /*
       * Align test bit with next bit of the message byte.
       */
      v = v >> 1;
    }
    return crc;
  }

  private static int augment_message_for_good_crc(int crc) {
    for (int i = 0; i < 16; i++) {
      boolean xor_flag = (crc & 0x8000) != 0;
      crc = crc << 1;
      crc = xor_flag ? (crc ^ POLY) : crc;
    }
    return crc;
  }
}
