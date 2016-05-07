package net.yck.kangaroo.commons;

public final class BuilderException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -2776948918597810627L;

  public BuilderException() {}

  public BuilderException(String message) {
    super(message);
  }

  public BuilderException(Throwable cause) {
    super(cause);
  }

  public BuilderException(String message, Throwable cause) {
    super(message, cause);
  }

  public BuilderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
