package net.yck.kangaroo.commons.util;

import org.junit.Assert;
import org.junit.Test;

public class Crc16TestSuite {

  byte[][] buffers = new byte[][] {//
      {}, //
      {'A'}, //
      {'1', '2', '3', '4', '5', '6', '7', '8', '9'}, //
      A256()};

  private static byte[] A256() {
    byte[] ret = new byte[256];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = 'A';
    }
    return ret;
  }

  @Test
  public void testFastCrc() {
    int[] expecteds = new int[] {0xFFFF, 0xB915, 0x29B1, 0xEA0B};
    int[] actuals = new int[expecteds.length];
    for (int i = 0; i < actuals.length; i++)
      actuals[i] = CRC16CCITT.fast_crc(buffers[i]);
    Assert.assertArrayEquals(expecteds, actuals);
  }

  @Test
  public void testGoodCrc() {
    int[] expecteds = new int[] {0x1D0F, 0x9479, 0xE5CC, 0xE938};
    int[] actuals = new int[expecteds.length];
    for (int i = 0; i < actuals.length; i++)
      actuals[i] = CRC16CCITT.good_crc(buffers[i]);
    Assert.assertArrayEquals(expecteds, actuals);
  }

}
