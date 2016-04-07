package net.yck.kangaroo.db.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

import net.yck.kangaroo.db.service.ThriftDbService;
import net.yck.kangaroo.db.service.thrift.DbService.Processor;

public class ThriftDbServer implements Runnable {

  private final static Logger logger = LogManager.getLogger(ThriftDbServer.class);

  @Override
  public void run() {
    try {
      TServerSocket serverTransport = new TServerSocket(ThriftDbService.DEF_PORT);
      Processor<ThriftDbService> processor =
          new Processor<ThriftDbService>(
              new ThriftDbService());

      Args args1 = new Args(serverTransport);
      args1.maxWorkerThreads(2048);
      args1.processor(processor);

      TServer server = new TThreadPoolServer(args1);
      logger.info(() -> "Thrift DbService started successfully.");

      boolean keepRunning = true;
      while (keepRunning) {
        try {
          server.serve();
        } catch (java.util.concurrent.RejectedExecutionException ree) {
          logger.warn(() -> "Execution rejected", ree);
        } catch (Exception e) {
          logger.error(() -> "Unexpected execution", e);
          keepRunning = false;
        }
      }
    } catch (TTransportException e) {
      logger.error(() -> "Failed to start Thrift DbService", e);
    }
  }

}
