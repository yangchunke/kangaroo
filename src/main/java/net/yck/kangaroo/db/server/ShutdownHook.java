package net.yck.kangaroo.db.server;

import org.apache.logging.log4j.Logger;

import net.yck.kangaroo.db.shared.AppBase;

public class ShutdownHook extends AppBase.Component implements Runnable {

  private final static Logger LOG = App.LOG;

  protected ShutdownHook(App app) {
    super(app);
  }

  @Override
  public void run() {
    LOG.info(() -> "shutting down...");
  }

  @Override
  public AppBase.Component initialize() throws Exception {
    Runtime.getRuntime().addShutdownHook(new Thread(this));
    return this;
  }

}
