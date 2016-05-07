package net.yck.kangaroo.commons;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Function;

import com.opencsv.CSVIterator;
import com.opencsv.CSVReader;

public class CSVTransformable<V> implements Closeable, Iterable<V> {

  private final CSVReader             reader;
  private final CSVIterator           iterator;
  private final Function<String[], V> transformer;

  private CSVTransformable(CSVReader reader, Function<String[], V> transformer) throws IOException {
    this.reader = reader;
    this.transformer = transformer;
    this.iterator = new CSVIterator(reader);
  }

  @Override
  public Iterator<V> iterator() {
    return new Iterator<V>() {

      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public V next() {
        return transformer.apply(iterator.next());
      }
    };
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  public static <V> Builder<V> builder() {
    return new Builder<V>();
  }

  public static class Builder<V> implements IBuilder<CSVTransformable<V>> {

    private CSVReader             reader;
    private Function<String[], V> transformer;

    @Override
    public CSVTransformable<V> build() throws BuilderException {
      checkArgument(reader != null && transformer != null);
      try {
        return new CSVTransformable<V>(reader, transformer);
      } catch (IOException e) {
        throw new BuilderException(e);
      }
    }

    public final CSVReader getReader() {
      return reader;
    }

    public final Builder<V> setReader(CSVReader reader) {
      this.reader = reader;
      return this;
    }

    public final Function<String[], V> getTransformer() {
      return transformer;
    }

    public final Builder<V> setTransformer(Function<String[], V> transformer) {
      this.transformer = transformer;
      return this;
    }

  }
}
