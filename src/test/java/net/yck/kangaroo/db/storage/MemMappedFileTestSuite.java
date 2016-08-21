package net.yck.kangaroo.db.storage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import net.yck.kangaroo.ITestSuite;

public class MemMappedFileTestSuite implements ITestSuite {

  private static int c_MaxLength = 4096;

  @Test
  public void test() throws IOException {

    byte[] expecteds = newBytes(new Random(System.currentTimeMillis()));

    String fileName = Paths.get(c_TmpDir, "MemMappedFileTestSuite.data").toString();

    try (MemMappedFile mmFile = MemMappedFile.builder().setFile(fileName).setMapMode(MapMode.READ_WRITE).setSize(expecteds.length).build()) {
      ByteBuffer bb = mmFile.getBuffer();
      bb.put(expecteds);
    }

    byte[] actuals = new byte[expecteds.length];
    try (MemMappedFile mmFile = MemMappedFile.builder().setFile(fileName).setMapMode(MapMode.READ_ONLY).setSize(expecteds.length).build()) {
      ByteBuffer bb = mmFile.getBuffer();
      bb.get(actuals);
    }

    Assert.assertArrayEquals(expecteds, actuals);
  }

  private byte[] newBytes(Random rand) {
    int len = rand.nextInt(c_MaxLength);
    byte[] ret = new byte[len];
    rand.nextBytes(ret);
    return ret;
  }
}
