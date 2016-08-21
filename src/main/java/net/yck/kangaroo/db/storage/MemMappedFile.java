package net.yck.kangaroo.db.storage;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MemMappedFile implements Closeable {

  private final static Logger    LOG        = LogManager.getLogger(MemMappedFile.class);

  private static String          READ       = "r";
  private static String          READ_WRITE = "rw";

  private final String           file;
  private final RandomAccessFile raf;
  private final MappedByteBuffer buffer;

  private MemMappedFile(String file, MapMode mapMode, long position, long size) throws IOException {
    this.file = file;
    this.raf = new RandomAccessFile(file, MapModeToRafMode(mapMode));
    try (FileChannel fileChannel = raf.getChannel()) {
      buffer = fileChannel.map(mapMode, position, size);
    }
    LOG.info("open MemMappedFile to " + file + " with mapMode - " + mapMode);
  }

  @Override
  public void close() throws IOException {
    LOG.info("close MemMappedFile to " + file);
    raf.close();
  }

  public final MappedByteBuffer getBuffer() {
    return buffer;
  }

  public static Builder builder() {
    return new Builder();
  }

  private static String MapModeToRafMode(FileChannel.MapMode mapMode) {
    if (MapMode.READ_ONLY == mapMode) {
      return READ;
    }
    return READ_WRITE;
  }

  static class Builder {

    private String  file;
    private MapMode mapMode  = MapMode.READ_ONLY;
    private long    position = 0;
    private long    size;

    public MemMappedFile build() throws IOException {
      return new MemMappedFile(file, mapMode, position, size);
    }

    public final MapMode getMapMode() {
      return mapMode;
    }

    public final Builder setMapMode(MapMode mode) {
      this.mapMode = mode;
      return this;
    }

    public final long getPosition() {
      return position;
    }

    public final Builder setPosition(long position) {
      this.position = position;
      return this;
    }

    public final long getSize() {
      return size;
    }

    public final Builder setSize(long size) {
      this.size = size;
      return this;
    }

    public final String getFile() {
      return file;
    }

    public final Builder setFile(String file) {
      this.file = file;
      return this;
    }
  }

}
