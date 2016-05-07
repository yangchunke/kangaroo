package net.yck.kangaroo.db.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.Logger;

import net.yck.kangaroo.commons.ResourceUtil;
import net.yck.kangaroo.db.shared.AppBase;

public abstract class DbServerBase extends AppBase.Component implements Runnable {

  private final static Logger LOG = App.LOG;
  
  private int port;
  private String version;

  DbServerBase(App app) {
    super(app);
  }

  @Override
  public AppBase.Component initialize() throws Exception {
    port = app.config.getProperty(getPortProperty().left, getPortProperty().right);
    return this;
  }

  /**
   * @return ImmutablePair<PropertyName, DefaultValue)
   */
  protected abstract ImmutablePair<String, Integer> getPortProperty();

  int getPort() {
    return port;
  }

  String getVersion() {
    if (StringUtils.isEmpty(version)) {
      synchronized (this) {
        if (StringUtils.isEmpty(version)) {
          try {
            Properties p = new Properties();
            p.load(new FileInputStream(ResourceUtil.getPathAsFile(this.getClass(), "version.txt")));
            version = p.getProperty("version") + ".v" + p.getProperty("build.date");
          } catch (IOException | URISyntaxException e) {
            version = "N/A";
            LOG.error(() -> "failed to load version", e);
          }
        }
      }
    }
    return version;
  }

}
