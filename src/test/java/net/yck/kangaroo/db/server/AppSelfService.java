package net.yck.kangaroo.db.server;

import org.junit.BeforeClass;

import net.yck.kangaroo.commons.ResourceUtil;

public abstract class AppSelfService {
  private final static Object lock = new Object();
  static App app = null;

  @BeforeClass
  public static void beforeClass() throws Exception {
    if (app == null) {
      synchronized (lock) {
        if (app == null) {
          app = new App(new String[] {"-c",
              ResourceUtil.getPathAsString(AppTestSuite.class, "dbserver.properties")});
          app.initialize();

          // Start avro db server in a separate thread
          new Thread(app.avroDbServer).start();
          // Start thrift server in a separate thread
          new Thread(app.thriftDbServer).start();

          try {
            // wait for the server start up
            Thread.sleep(500);
          } catch (InterruptedException e) {
          }
        }
      }
    }
  }
}
