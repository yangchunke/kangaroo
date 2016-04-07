package net.yck.kangaroo.db.shared;

public interface IConfigurable {
  final static String THRIFT_DBSERVER_ENABLED = "thrift.dbserver.enabled";
  final static String THRIFT_DBSERVER_PORT = "thrift.dbserver.port";
  final static String THRIFT_MAX_WORKER_THREADS = "thrift.max.worker.threads";

  final static String AVRO_DBSERVER_ENABLED = "avro.dbserver.enabled";
  final static String AVRO_DBSERVER_PORT = "avro.dbserver.port";
}
