package net.yck.kangaroo.db.server;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import net.yck.kangaroo.db.client.AvroDbClient;
import net.yck.kangaroo.db.client.ThriftDbClient;

public class AppTestSuite extends AppSelfService {

  @Test
  public void testPing() {
    try (ThriftDbClient client =
        ThriftDbClient.getClient("localhost", app.thriftDbServer.getPort())) {
      String v = client.ping();
      System.out.println(v);
      Assert.assertFalse(StringUtils.equalsIgnoreCase("N/A", v));
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail("failed to ping thrift dbserver.");
    }


    try (AvroDbClient client = AvroDbClient.getClient("localhost", app.avroDbServer.getPort())) {
      CharSequence v = client.ping();
      System.out.println(v);
      Assert.assertFalse(v.equals("N/A"));
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail("failed to ping avro dbserver.");
    }
  }

}
