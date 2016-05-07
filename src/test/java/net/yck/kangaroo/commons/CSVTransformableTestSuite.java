package net.yck.kangaroo.commons;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import com.opencsv.CSVReader;

public class CSVTransformableTestSuite {

  @Test
  public void testBatting() throws URISyntaxException, IOException, BuilderException {

    String file = ResourceUtil.getPathAsString(this.getClass(), "baseballdatabank-master/core", "Batting.csv.gz");
    CSVReader reader = new CSVReaderBuilder(file).skipLines(1).build();

    final int playerIdIdx = 0;
    final int yearIdIdx = 1;
    Function<String[], ImmutablePair<String, Integer>> transformer =
        (String[] args) -> ImmutablePair.of(args[playerIdIdx], Integer.parseInt(args[yearIdIdx]));

    final int expectedCnt = 101332;
    int actualCnt = 0;
    try (CSVTransformable<ImmutablePair<String, Integer>> iteratable = getIterable(reader, transformer)) {
      Iterator<ImmutablePair<String, Integer>> iterator = iteratable.iterator();
      while (iterator.hasNext()) {
        actualCnt++;
        iterator.next();
      }
    }

    Assert.assertEquals(expectedCnt, actualCnt);
  }

  @Test
  public void testTeams() throws URISyntaxException, IOException, BuilderException {

    String file = ResourceUtil.getPathAsString(this.getClass(), "baseballdatabank-master/core", "Teams.csv.gz");
    CSVReader reader = new CSVReaderBuilder(file).skipLines(1).build();

    final int yearIdIdx = 0;
    final int teamIdIdx = 2;
    Function<String[], ImmutablePair<String, Integer>> transformer =
        (String[] args) -> ImmutablePair.of(args[teamIdIdx], Integer.parseInt(args[yearIdIdx]));

    final int expectedCnt = 2805;
    int actualCnt = 0;
    try (CSVTransformable<ImmutablePair<String, Integer>> iteratable = getIterable(reader, transformer)) {
      Iterator<ImmutablePair<String, Integer>> iterator = iteratable.iterator();
      while (iterator.hasNext()) {
        actualCnt++;
        iterator.next();
      }
    }

    Assert.assertEquals(expectedCnt, actualCnt);
  }

  private CSVTransformable<ImmutablePair<String, Integer>> getIterable(CSVReader reader,
      Function<String[], ImmutablePair<String, Integer>> transformer) throws BuilderException {
    CSVTransformable.Builder<ImmutablePair<String, Integer>> builder =
        CSVTransformable.<ImmutablePair<String, Integer>>builder();
    return builder.setReader(reader).setTransformer(transformer).build();
  }
}
