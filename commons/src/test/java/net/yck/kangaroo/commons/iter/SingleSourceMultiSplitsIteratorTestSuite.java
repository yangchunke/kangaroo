package net.yck.kangaroo.commons.iter;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class SingleSourceMultiSplitsIteratorTestSuite {

  @Test
  public void test() {

    List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
    Iterator<Integer> source = list.iterator();
    SingleSourceMultiSplitsIterator.Splitter<Integer, Integer> splitter = (Integer v) -> {
      return v;
    };

    SingleSourceMultiSplitsIterator.Builder<Integer, Integer, Integer> builder =
        SingleSourceMultiSplitsIterator.<Integer, Integer, Integer>builder()//
            .source(source)//
            .splitter(splitter);

    for (int i = 1; i <= list.size(); i++) {
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

    Assert.assertEquals(list.size(), results.size());


  }

}
