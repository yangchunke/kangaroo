package net.yck.kangaroo.commons.iter;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.function.Function;

import com.opencsv.CSVIterator;
import com.opencsv.CSVReader;

public class CSVTransformingIterator<V> implements Closeable, Iterator<V> {

  private final CSVReader reader;
  private final CSVIterator iterator;
  private final Function<String[], V> transformer;

  public CSVTransformingIterator(CSVReader reader, Function<String[], V> transformer)
      throws IOException {
    checkArgument(reader != null && transformer != null);
    this.reader = reader;
    this.transformer = transformer;
    this.iterator = new CSVIterator(reader);
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public V next() {
    return transformer.apply(iterator.next());
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

}
