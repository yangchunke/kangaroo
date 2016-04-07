package net.yck.kangaroo.db.service;

import org.apache.avro.AvroRemoteException;

import net.yck.kangaroo.db.service.avro.DbService;

public class AvroDbService implements DbService {

  public final static int DEF_PORT = 65111;

  @Override
  public Void ping() throws AvroRemoteException {
    return null;
  }

}
