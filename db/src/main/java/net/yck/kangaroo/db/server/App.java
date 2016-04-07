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

import net.yck.kangaroo.db.shared.AppBase;

public class App extends AppBase {

  private final static Logger LOG = LogManager.getLogger(App.class);

  ThriftDbServer thriftDbServer;
  AvroDbServer avroDbServer;

  public static void main(String[] args) {
    App app = new App(args);
    try {
      app.initialize();
      app.start();
    } catch (Exception e) {
      LOG.fatal(() -> "failed to start the application.", e);
    }
  }

  App(String args[]) {
    super(args);
  }

  void initialize() throws Exception {
    thriftDbServer = (ThriftDbServer) new ThriftDbServer(this).initialize();
    avroDbServer = (AvroDbServer) new AvroDbServer(this).initialize();
  }

  void start() {

    final List<Runnable> runnables = Arrays.asList(thriftDbServer, avroDbServer);

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
          LOG.warn(() -> "execute", t);
          cdl.countDown();
        }
      });
    });

    try {
      cdl.await();
    } catch (InterruptedException e) {
      LOG.error(() -> "execute", e);
    } finally {
      pool.shutdown();
    }
  }
}
