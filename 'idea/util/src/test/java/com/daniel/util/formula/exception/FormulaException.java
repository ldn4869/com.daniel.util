package com.daniel.util.formula.exception;

public class FormulaException extends RuntimeException {

  public FormulaException() {
  }

  public FormulaException(String message) {
    super(message);
  }

  public FormulaException(String message, Throwable cause) {
    super(message, cause);
  }

  public FormulaException(Throwable cause) {
    super(cause);
  }

  public FormulaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
