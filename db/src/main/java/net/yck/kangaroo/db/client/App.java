package net.yck.kangaroo.db.client;

import java.io.IOException;

import org.apache.thrift.transport.TTransportException;

import net.yck.kangaroo.db.service.AvroDbService;
import net.yck.kangaroo.db.service.ThriftDbService;

public class App {

  public static void main(String[] args) throws TTransportException, IOException {
    try (ThriftDbClient client = ThriftDbClient.getClient("localhost", ThriftDbService.DEF_PORT)) {
      client.ping();
      System.out.println("thrift service pinged");
    } catch (Exception e) {
      e.printStackTrace();
    }

    try (AvroDbClient client = AvroDbClient.getClient("localhost", AvroDbService.DEF_PORT)) {
      client.ping();
      System.out.println("avro service pinged");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
