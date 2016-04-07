package net.yck.kangaroo.db.server;

import java.net.InetSocketAddress;

import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificResponder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.yck.kangaroo.db.service.AvroDbService;
import net.yck.kangaroo.db.service.avro.DbService;

public class AvroDbServer implements Runnable {

  private final static Logger logger = LogManager.getLogger(AvroDbServer.class);

  @Override
  public void run() {

    Server server = null;
    try {
      server = new NettyServer(new SpecificResponder(DbService.class, new AvroDbService()),
          new InetSocketAddress("localhost", AvroDbService.DEF_PORT));
      logger.info(() -> "Avro DbService started successfully.");
      server.join();
    } catch (Exception e) {
      logger.error(() -> "Failed to start Avro DbService", e);
    } finally {
      if (server != null) {
        server.close();
      }
    }
  }

}
