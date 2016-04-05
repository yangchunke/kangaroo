package net.yck.kangaroo.commons.util;

import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtil {

  private final static Logger logger = LogManager.getLogger(LogUtil.class);

  public static void debug(Supplier<String> msgSupplier) {
    logger.debug(msgSupplier);
  }

  public static void info(Supplier<String> msgSupplier) {
    logger.info(msgSupplier);
  }

  public static void warn(Supplier<String> msgSupplier, Throwable t) {
    logger.warn(msgSupplier, t);
  }

  public static void error(Supplier<String> msgSupplier, Throwable t) {
    logger.error(msgSupplier, t);
  }

  public static void fatal(Supplier<String> msgSupplier, Throwable t) {
    logger.fatal(msgSupplier, t);
  }
}
