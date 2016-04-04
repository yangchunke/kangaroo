package net.yck.kangaroo.commons.parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class NumberExpressionTestSuite {

  private static int c_min = 0;
  private static int c_max = 100000;

  @Test
  public void testPattern1() {
    final String pattern = "1,3-6,100-200,666,1000-3000/5,400-/7";
    NumberExpression ne = new NumberExpression(pattern);

    assertTrue("1 == ne.getMinimum()", 1 == ne.getMinimum());
    assertTrue("Integer.MAX_VALUE == ne.getMaximum()", Integer.MAX_VALUE == ne.getMaximum());

    for (int i = c_min; i <= c_max; i++) {
      if (i == 1 || (i >= 3 && i <= 6) || (i >= 100 && i <= 200) || i == 666
          || (i >= 1000 && i <= 3000 & ((i - 1000) % 5 == 0)) || (i >= 400 && (i - 400) % 7 == 0)) {
        assertTrue("ne.matches(" + i + ")", ne.matches(i));
      } else {
        assertFalse("!ne.matches(" + i + ")", ne.matches(i));
      }
    }
  }

  @Test
  public void testPattern2() {
    final String pattern = "-100,102-";
    NumberExpression ne = new NumberExpression(pattern);

    assertTrue("0 == ne.getMinimum()", 0 == ne.getMinimum());
    assertTrue("Integer.MAX_VALUE == ne.getMaximum()", Integer.MAX_VALUE == ne.getMaximum());

    for (int i = c_min; i <= c_max; i++) {
      if (i <= 100 || i >= 102) {
        assertTrue("ne.matches(" + i + ")", ne.matches(i));
      } else {
        assertFalse("!ne.matches(" + i + ")", ne.matches(i));
      }
    }
  }

  @Test
  public void testPattern3() {
    final String pattern = "*";
    NumberExpression ne = new NumberExpression(pattern);

    assertTrue("0 == ne.getMinimum()", 0 == ne.getMinimum());
    assertTrue("Integer.MAX_VALUE == ne.getMaximum()", Integer.MAX_VALUE == ne.getMaximum());

    for (int i = c_min; i <= c_max; i++) {
      assertTrue("ne.matches(" + i + ")", ne.matches(i));
    }
  }

  @Test
  public void testEvenNumbers() {
    doTestOddEven("*/2", 0, c_max, 0, Integer.MAX_VALUE);
    doTestOddEven("0-" + c_max + "e", 0, c_max, 0, c_max);
  }

  @Test
  public void testOddNumbers() {
    doTestOddEven("1-/2", 1, c_max, 1, Integer.MAX_VALUE);
    doTestOddEven("1-" + c_max + "o", 1, c_max, 1, c_max);
  }

  @Test
  public void testInvalidPatterns() {

    @SuppressWarnings("unused")
    NumberExpression ne = null;

    boolean exceptionCaught = false;
    try {
      ne = new NumberExpression(StringUtils.EMPTY);
    } catch (IllegalArgumentException e) {
      exceptionCaught = true;
    }
    assertTrue(StringUtils.EMPTY, exceptionCaught);
  }

  @Test
  public void testIterator() {
    final String pattern = "1,3-6,100-200,666,1000-3000/5,4000-10000e";

    int expected = 0;
    expected += 1; // "1"
    expected += (6 - 3 + 1); // "3-6"
    expected += (200 - 100 + 1); // "100-200"
    expected += 1; // "666";
    expected += ((3000 - 1000) / 5 + 1); // "1000-3000/5"
    expected += ((10000 - 4000) / 2 + 1); // "4000-10000e"

    NumberExpression ne = new NumberExpression(pattern);
    Iterator<Integer> iter = ne.iterator();
    int actual = 0;
    while (iter.hasNext()) {
      actual++;
      int val = iter.next();
      Assert.assertTrue("ne.matches(" + val + ")", ne.matches(val));
    }
    Assert.assertEquals(expected, actual);

    boolean exceptionCaught = false;
    try {
      iter.next();
    } catch (NoSuchElementException nsee) {
      exceptionCaught = true;
    }
    Assert.assertTrue("exception should be caught", exceptionCaught);
  }

  private void doTestOddEven(final String pattern, int start, int end, int min, int max) {
    NumberExpression ne = new NumberExpression(pattern);

    assertTrue(min + " == ne.getMinimum()", min == ne.getMinimum());
    assertTrue(max + " == ne.getMaximum()", max == ne.getMaximum());

    for (int i = start; i <= end; i += 2) {
      assertTrue("ne.matches(" + i + ") - " + pattern, ne.matches(i));
    }
  }
}
