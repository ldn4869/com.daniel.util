package com.daniel.util.fomula.exception;

public class FomulaException extends RuntimeException {

  public FomulaException() {
  }

  public FomulaException(String message) {
    super(message);
  }

  public FomulaException(String message, Throwable cause) {
    super(message, cause);
  }

  public FomulaException(Throwable cause) {
    super(cause);
  }

  public FomulaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
