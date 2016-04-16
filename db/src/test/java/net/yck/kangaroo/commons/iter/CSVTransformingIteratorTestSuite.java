package net.yck.kangaroo.commons.iter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Assert;
import org.junit.Test;

import com.opencsv.CSVReader;

import net.yck.kangaroo.commons.util.CSVReaderBuilder;
import net.yck.kangaroo.commons.util.ResourceUtil;

public class CSVTransformingIteratorTestSuite {

  @Test
  public void testBatting() throws URISyntaxException, IOException {

    String file = ResourceUtil.getPathAsString(this.getClass(), "baseballdatabank-master/core",
        "Batting.csv.gz");
    CSVReader reader = new CSVReaderBuilder(file).skipLines(1).build();

    final int playerIdIdx = 0;
    final int yearIdIdx = 1;
    Function<String[], ImmutablePair<String, Integer>> transformer =
        (String[] args) -> ImmutablePair.of(args[playerIdIdx], Integer.parseInt(args[yearIdIdx]));

    final int expectedCnt = 101332;
    int actualCnt = 0;
    try (CSVTransformingIterator<ImmutablePair<String, Integer>> iterator =
        new CSVTransformingIterator<>(reader, transformer)) {
      while(iterator.hasNext()){
        actualCnt++;
        iterator.next();
      }
    }
    
    Assert.assertEquals(expectedCnt, actualCnt);
  }
  
  @Test
  public void testTeams() throws URISyntaxException, IOException {

    String file = ResourceUtil.getPathAsString(this.getClass(), "baseballdatabank-master/core",
        "Teams.csv.gz");
    CSVReader reader = new CSVReaderBuilder(file).skipLines(1).build();

    final int yearIdIdx = 0;
    final int teamIdIdx = 2;
    Function<String[], ImmutablePair<String, Integer>> transformer =
        (String[] args) -> ImmutablePair.of(args[teamIdIdx], Integer.parseInt(args[yearIdIdx]));

    final int expectedCnt = 2805;
    int actualCnt = 0;
    try (CSVTransformingIterator<ImmutablePair<String, Integer>> iterator =
        new CSVTransformingIterator<>(reader, transformer)) {
      while(iterator.hasNext()){
        actualCnt++;
        iterator.next();
      }
    }
    
    Assert.assertEquals(expectedCnt, actualCnt);
  }
}
