package net.yck.kangaroo.db.shared;

import java.net.URISyntaxException;

import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import net.yck.kangaroo.commons.util.ResourceUtil;

public class ConfiguratorTestSuite {

  private final static String PROP_HOST = "database.host";
  private final static String VAL_HOST = "db.yck.net";

  private final static String PROP_PORT = "database.port";
  private final static Number VAL_PORT = 9170;

  private final static String PROP_LIVE = "database.live";
  private final static boolean VAL_LIVE = true;

  @Test
  public void testConfigurationFromFile() throws URISyntaxException, ParseException {

    String file = ResourceUtil.getPathAsString(this.getClass(), "test.properties");

    Configurator configurator = Configurator.builder().addOption(Configurator.OPT_CONFIGURATION)
        .args(new String[] {"-c", file}).build();

    verify("testConfigurationFromFile", configurator);
  }

  @Test
  public void testConfigurationFromArgs() throws URISyntaxException, ParseException {

    Configurator configurator = Configurator.builder()//
        .addOption("p", PROP_PORT, "port", PROP_PORT)//
        .addOption("h", PROP_HOST, "host", PROP_HOST)//
        .addOption("l", PROP_LIVE, "live", PROP_LIVE)
        .args(new String[] {//
            "--" + PROP_HOST, VAL_HOST, //
            "--" + PROP_PORT, VAL_PORT.toString(), //
            "--" + PROP_LIVE, Boolean.toString(VAL_LIVE)})
        .build();

    verify("testConfigurationFromArgs", configurator);
  }

  @Test
  public void testConfigurationFromShortOptProperties() throws URISyntaxException, ParseException {

    final String propHeader = "-" + Configurator.OPT_PROPERTY.getOpt();
    Configurator configurator = Configurator.builder()//
        .addOption(Configurator.OPT_PROPERTY)
        .args(new String[] {//
            propHeader + PROP_HOST + "=" + VAL_HOST, //
            propHeader + PROP_PORT + "=" + VAL_PORT.toString(), //
            propHeader + PROP_LIVE + "=" + Boolean.toString(VAL_LIVE)})
        .build();

    verify("testConfigurationFromProperties", configurator);
  }

  @Test
  public void testConfigurationFromLongOptProperties() throws URISyntaxException, ParseException {

    final String propHeader = "--" + Configurator.OPT_PROPERTY.getLongOpt();
    Configurator configurator = Configurator.builder()//
        .addOption(Configurator.OPT_PROPERTY)
        .args(new String[] {//
            propHeader, PROP_HOST + "=" + VAL_HOST, //
            propHeader, PROP_PORT + "=" + VAL_PORT.toString(), //
            propHeader, PROP_LIVE + "=" + Boolean.toString(VAL_LIVE)})
        .build();

    verify("testConfigurationFromProperties", configurator);
  }

  private void verify(String caller, Configurator configurator) throws ParseException {
    Assert.assertEquals(caller, VAL_HOST, configurator.getProperty(PROP_HOST, StringUtils.EMPTY));
    Assert.assertEquals(caller, VAL_PORT, configurator.getProperty(PROP_PORT, Integer.MIN_VALUE));
    Assert.assertEquals(caller, VAL_LIVE, configurator.getProperty(PROP_LIVE, Boolean.FALSE));
  }

}
