package net.yck.kangaroo.db.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import net.yck.kangaroo.commons.util.LogUtil;
import net.yck.kangaroo.commons.util.ResourceUtil;
import net.yck.kangaroo.db.shared.AppBase.Component;

public abstract class DbServerBase extends App.Component implements Runnable {

  private int port;
  private String version;

  DbServerBase(App app) {
    super(app);
  }

  @Override
  public Component initialize() throws Exception {
    port = app.config.getProperty(getPortProperty().left, getPortProperty().right);
    return this;
  }

  protected abstract ImmutablePair<String, Integer> getPortProperty();

  public int getPort() {
    return port;
  }

  public String getVersion() {
    if (StringUtils.isEmpty(version)) {
      synchronized (this) {
        if (StringUtils.isEmpty(version)) {
          try {
            Properties p = new Properties();
            p.load(
                new FileInputStream(ResourceUtil.getPathAsFile(this.getClass(), "version.txt")));
            version = p.getProperty("version") + ".v" + p.getProperty("build.date");
          } catch (IOException | URISyntaxException e) {
            version = "N/A";
            LogUtil.error(() -> "failed to load version", e);
          }
        }
      }
    }
    return version;
  }

}
