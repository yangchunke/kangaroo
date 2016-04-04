package net.yck.kangaroo.commons.util;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtil {

  private final static Logger def_logger = LogManager.getLogger(LogUtil.class);

  private static Logger resolve(Logger logger) {
    return logger != null ? logger : def_logger;
  }

  public static void debug(Logger logger, Supplier<String> msgSupplier) {
    Logger lggr = resolve(logger);
    if (lggr.isDebugEnabled()) {
      lggr.debug(msgSupplier.get());
    }
  }

  public static void info(Logger logger, Supplier<String> msgSupplier) {
    Logger lggr = resolve(logger);
    if (lggr.isInfoEnabled()) {
      lggr.info(msgSupplier.get());
    }
  }

  public static void warn(Logger logger, Supplier<String> msgSupplier, Throwable t) {
    Logger lggr = resolve(logger);
    if (lggr.isWarnEnabled()) {
      lggr.warn(msgSupplier.get(), t);
    }
  }

  public static void error(Logger logger, Supplier<String> msgSupplier, Throwable t) {
    Logger lggr = resolve(logger);
    if (lggr.isErrorEnabled()) {
      lggr.error(msgSupplier.get(), t);
    }
  }
}
