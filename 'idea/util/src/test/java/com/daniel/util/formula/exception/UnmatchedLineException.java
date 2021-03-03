package com.daniel.util.formula.exception;

public class UnmatchedLineException extends RuntimeException {

  public UnmatchedLineException() {
  }

  public UnmatchedLineException(String message) {
    super(message);
  }

  public UnmatchedLineException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnmatchedLineException(Throwable cause) {
    super(cause);
  }

  public UnmatchedLineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
