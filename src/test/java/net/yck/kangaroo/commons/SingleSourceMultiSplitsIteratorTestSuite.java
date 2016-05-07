package net.yck.kangaroo.commons;

import java.util.Iterator;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.yck.kangaroo.commons.NumberExpression;
import net.yck.kangaroo.commons.SingleSourceMultiSplitsIterator;

public class SingleSourceMultiSplitsIteratorTestSuite {

  @Test
  public void test() {

    NumberExpression numExp = new NumberExpression("1-5");
    final int size = numExp.getMaximum() - numExp.getMinimum() + 1;
    Iterator<Integer> source = numExp.iterator();
    SingleSourceMultiSplitsIterator.Splitter<Integer, Integer> splitter = (Integer v) -> {
      return v;
    };

    SingleSourceMultiSplitsIterator.Builder<Integer, Integer, Integer> builder =
        SingleSourceMultiSplitsIterator.<Integer, Integer, Integer>builder()//
            .source(source)//
            .splitter(splitter);

    for (int i = 1; i <= size; i++) {
      final Integer id = i;
      builder
          .addConsumer(new SingleSourceMultiSplitsIterator.Consumer<Integer, Integer, Integer>() {

            int sum = 0;

            @Override
            protected Integer splitId() {
              return id;
            }

            @Override
            protected Integer execute() {
              while (iterator().hasNext()) {
                sum += iterator().next();
              }

              return sum;
            }
          });
    }

    SingleSourceMultiSplitsIterator<Integer, Integer>.Executor<Integer> executor = builder.build();

    Map<Integer, Integer> results = executor.execute();

    results.forEach((id, sum) -> {
      Assert.assertEquals(id, sum);
    });

    Assert.assertEquals(size, results.size());
  }

}
