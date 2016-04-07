package net.yck.kangaroo.db.server;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class App {

  private final static Logger logger = LogManager.getLogger(App.class);

  public static void main(String[] args) {

    final List<Runnable> runnables = Arrays.asList(new ThriftDbServer(), new AvroDbServer());

    final ListeningExecutorService pool =
        MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(runnables.size()));

    final CountDownLatch cdl = new CountDownLatch(runnables.size());

    runnables.forEach(runnable -> {

      Futures.<Void>addCallback(pool.submit(runnable, null), new FutureCallback<Void>() {

        @Override
        public void onSuccess(Void result) {
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
  }

}
