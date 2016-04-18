package net.yck.kangaroo.db.server;

import org.apache.logging.log4j.Logger;

public class ShutdownHook extends App.Component implements Runnable {

  private final static Logger LOG = App.LOG;

  protected ShutdownHook(App app) {
    super(app);
  }

  @Override
  public void run() {
    LOG.info(() -> "shutting down...");
  }

  @Override
  public App.Component initialize() throws Exception {
    Runtime.getRuntime().addShutdownHook(new Thread(this));
    return this;
  }

}
