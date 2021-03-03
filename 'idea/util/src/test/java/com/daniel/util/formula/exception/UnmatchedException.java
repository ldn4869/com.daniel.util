package com.daniel.util.formula.exception;

/**
 * 匹配失败
 */
public class UnmatchedException extends RuntimeException {

  public UnmatchedException() {
  }

  public UnmatchedException(String message) {
    super(message);
  }

  public UnmatchedException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnmatchedException(Throwable cause) {
    super(cause);
  }

  public UnmatchedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
