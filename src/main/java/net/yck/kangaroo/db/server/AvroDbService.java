package net.yck.kangaroo.db.server;

import org.apache.avro.AvroRemoteException;

import net.yck.kangaroo.db.service.avro.DbService;

class AvroDbService implements DbService {

  private final AvroDbServer server;

  public AvroDbService(AvroDbServer server) {
    this.server = server;
  }

  @Override
  public CharSequence ping() throws AvroRemoteException {
    return server.getVersion();
  }

}
