package net.yck.kangaroo.db.service;

import org.apache.thrift.TException;

import net.yck.kangaroo.db.service.thrift.DbService;

public class ThriftDbService implements DbService.Iface{

  public final static int DEF_PORT = 10719;
  
  @Override
  public void ping() throws TException {
    // no-op
  }

}
