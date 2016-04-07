package net.yck.kangaroo.db.server;

import org.apache.thrift.TException;

import net.yck.kangaroo.db.service.thrift.DbService;

class ThriftDbService implements DbService.Iface {

  private final ThriftDbServer server;

  public ThriftDbService(ThriftDbServer server) {
    this.server = server;
  }

  @Override
  public String ping() throws TException {
    return server.getVersion();
  }

}
