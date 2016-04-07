package net.yck.kangaroo.db.client;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.AvroRemoteException;
import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import net.yck.kangaroo.db.service.avro.DbService;

public class AvroDbClient implements net.yck.kangaroo.db.service.avro.DbService, AutoCloseable {

  private final NettyTransceiver client;
  private final DbService proxy;

  private AvroDbClient(NettyTransceiver client, DbService proxy) {
    this.client = client;
    this.proxy = proxy;
  }

  public static AvroDbClient getClient(String host, int port) throws IOException {
    NettyTransceiver client = new NettyTransceiver(new InetSocketAddress(host, port));
    DbService proxy = (DbService) SpecificRequestor.getClient(DbService.class, client);
    return new AvroDbClient(client, proxy);
  }

  @Override
  public void close() throws Exception {
    client.close();
  }

  @Override
  public Void ping() throws AvroRemoteException {
    return proxy.ping();
  }

}
