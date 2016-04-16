package net.yck.kangaroo.commons.iter;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class SingleSourceMultiSplitsIterator<V, S> implements Iterator<V> {

  private final static Logger logger = LogManager.getLogger(SingleSourceMultiSplitsIterator.class);

  private AtomicLong processedCount = new AtomicLong();
  private final Map<S, ConcurrentLinkedQueue<V>> splitToQueueMap = new HashMap<>();
  private final ConcurrentHashMap<Long, S> threadToSplitMap = new ConcurrentHashMap<>();

  private final Iterator<V> source;
  private final Splitter<V, S> splitter;
  private boolean strictOrder;

  private SingleSourceMultiSplitsIterator(Iterator<V> source, Splitter<V, S> splitter) {
    this.source = source;
    this.splitter = splitter;
  }

  @Override
  public boolean hasNext() {
    ConcurrentLinkedQueue<V> currentQueue = getCurrentQueue();

    while (currentQueue != null && currentQueue.isEmpty()) {

      boolean shouldBreak = false;

      try {
        shouldBreak = strictOrder ? strictOrderCheck(currentQueue) : normalOrderCheck(currentQueue);
      } catch (Exception e) {
        logger.error(() -> "hasNext", e);
      }

      if (shouldBreak) {
        break;
      }
    }

    return currentQueue != null && !currentQueue.isEmpty();
  }

  @Override
  public V next() {
    processedCount.incrementAndGet();
    return getCurrentQueue().remove();
  }

  <O> Executor<O> executor(List<Consumer<V, S, O>> consumers) {
    return this.new Executor<O>(consumers);
  }

  <O> void addConsumer(Consumer<V, S, O> consumer) {
    splitToQueueMap.put(consumer.splitId(), new ConcurrentLinkedQueue<>());
    consumer.iterator = this;
  }

  private synchronized boolean strictOrderCheck(ConcurrentLinkedQueue<V> currentQueue) {
    if (!currentQueue.isEmpty()) {
      return true;
    }

    if (!source.hasNext()) {
      return true;
    }

    return append(source.next(), currentQueue);
  }

  private boolean normalOrderCheck(ConcurrentLinkedQueue<V> currentQueue) {

    V input = null;

    synchronized (source) {
      if (!currentQueue.isEmpty()) {
        return true;
      }

      if (!source.hasNext()) {
        return true;
      }

      input = source.next();
    }

    return append(input, currentQueue);
  }

  private boolean append(V input, final ConcurrentLinkedQueue<V> currentQueue) {
    boolean shouldBreak = false;
    if (input != null) {
      ConcurrentLinkedQueue<V> queue = splitToQueueMap.get(splitter.apply(input));
      if (queue != null) {
        logger.debug(() -> "append " + input + " to " + queue.toString());
        queue.add(input);
        shouldBreak = (currentQueue == queue);
      }
    }
    return shouldBreak;
  }

  private SingleSourceMultiSplitsIterator<V, S> strictOrder(boolean strictOrder) {
    this.strictOrder = strictOrder;
    return this;
  }

  private ConcurrentLinkedQueue<V> getCurrentQueue() {
    S splitId = threadToSplitMap.get(Thread.currentThread().getId());
    return splitToQueueMap.get(splitId);
  }

  private void registerThread(final long threadId, final S splitId) {
    if (threadToSplitMap.containsKey(splitId)) {
      throw new IllegalArgumentException(
          "threadId" + threadId + "has been occupied by split " + splitId);
    }
    logger.info(() -> "register thread " + threadId + " with split " + splitId);
    threadToSplitMap.put(threadId, splitId);
  }

  private boolean readyToRun() {
    return threadToSplitMap.size() == splitToQueueMap.size();
  }

  public interface Splitter<V, S> extends Function<V, S> {
  }

  public static abstract class Consumer<V, S, O> implements Callable<O> {

    private SingleSourceMultiSplitsIterator<V, S> iterator;

    Consumer<V, S, O> iterator(SingleSourceMultiSplitsIterator<V, S> iterator) {
      this.iterator = iterator;
      return this;
    }

    SingleSourceMultiSplitsIterator<V, S> iterator() {
      return iterator;
    }

    protected abstract S splitId();

    protected abstract O execute();

    private boolean ready2Run() {

      iterator().registerThread(Thread.currentThread().getId(), splitId());

      boolean ready2Run = true;

      while (!iterator().readyToRun()) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          ready2Run = false;
          break;
        }
      }

      return ready2Run;
    }

    @Override
    public O call() throws Exception {
      O output = null;
      if (ready2Run()) {
        output = execute();
      }
      return output;
    }
  }

  public class Executor<O> {

    private List<Consumer<V, S, O>> consumers;

    Executor(List<Consumer<V, S, O>> consumers) {
      this.consumers = consumers;
    }

    private SingleSourceMultiSplitsIterator<V, S> iterator() {
      return SingleSourceMultiSplitsIterator.this;
    }

    public Map<S, O> execute() {

      consumers.forEach(consumer -> {
        iterator().addConsumer(consumer);
      });

      final int size = consumers.size();
      final ListeningExecutorService pool =
          MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(size));

      final Map<S, O> results = new ConcurrentHashMap<S, O>();
      final CountDownLatch cdl = new CountDownLatch(size);

      consumers.forEach(consumer -> {

        Futures.addCallback(pool.submit(consumer), new FutureCallback<O>() {

          @Override
          public void onSuccess(O result) {
            results.put(consumer.splitId(), result);
            cdl.countDown();
          }

          @Override
          public void onFailure(Throwable t) {
            logger.warn(() -> "execute", t);
            cdl.countDown();
          }
        });
      });

      try {
        cdl.await();
      } catch (InterruptedException e) {
        logger.error(() -> "execute", e);
      } finally {
        pool.shutdown();
      }

      return results;
    }
  }

  public static <V, S, O> Builder<V, S, O> builder() {
    return new Builder<V, S, O>();
  }

  public static class Builder<V, S, O> {

    Iterator<V> source;
    Splitter<V, S> splitter;
    private List<Consumer<V, S, O>> consumers = new ArrayList<>();
    boolean strictOrder;

    Builder() {}

    public Builder<V, S, O> source(Iterator<V> source) {
      this.source = source;
      return this;
    }

    public Builder<V, S, O> splitter(Splitter<V, S> splitter) {
      this.splitter = splitter;
      return this;
    }

    public Builder<V, S, O> strictOrder(boolean strictOrder) {
      this.strictOrder = strictOrder;
      return this;
    }

    public Builder<V, S, O> addConsumer(Consumer<V, S, O> consumer) {
      consumers.add(consumer);
      return this;
    }


    public SingleSourceMultiSplitsIterator<V, S>.Executor<O> build() {
      checkState(source != null, "source is null");
      checkState(splitter != null, "splitter is null");
      SingleSourceMultiSplitsIterator<V, S>.Executor<O> executor =
          new SingleSourceMultiSplitsIterator<V, S>(source, splitter)//
              .strictOrder(strictOrder).executor(consumers);

      return executor;
    }
  }
}
