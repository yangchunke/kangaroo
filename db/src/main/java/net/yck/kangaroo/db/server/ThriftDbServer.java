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

  private final static Logger LOG = LogManager.getLogger(ThriftDbServer.class);

  private final static int DEF_MAX_WORKER_THREADS = 2048;

  @Override
  public void run() {
    try {
      TServerSocket serverTransport = new TServerSocket(ThriftDbService.DEF_PORT);
      Processor<ThriftDbService> processor = new Processor<ThriftDbService>(new ThriftDbService());

      Args args = new Args(serverTransport).maxWorkerThreads(DEF_MAX_WORKER_THREADS).processor(processor);

      TServer server = new TThreadPoolServer(args);
      LOG.info(() -> "Thrift DbService started successfully.");

      boolean keepRunning = true;
      while (keepRunning) {
        try {
          server.serve();
        } catch (java.util.concurrent.RejectedExecutionException ree) {
          LOG.warn(() -> "Execution rejected", ree);
        } catch (Exception e) {
          LOG.error(() -> "Unexpected execution", e);
          keepRunning = false;
        }
      }
    } catch (TTransportException e) {
      LOG.error(() -> "Failed to start Thrift DbService", e);
    }
  }

}
