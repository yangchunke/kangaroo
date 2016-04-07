package net.yck.kangaroo.db.shared;

import net.yck.kangaroo.db.server.App;

public abstract class AppBase {

  public final Configurator config;

  public AppBase(String args[]) {
    config = configuratorBuilder(args).build();
  }

  protected Configurator.Builder configuratorBuilder(String args[]) {
    return Configurator.builder().addOption(Configurator.OPT_CONFIGURATION)
        .addOption(Configurator.OPT_PROPERTY).args(args);
  }

  public abstract static class Component implements IConfigurable {

    public final App app;

    protected Component(App app) {
      this.app = app;
    }

    public abstract Component initialize() throws Exception;
  }

}

