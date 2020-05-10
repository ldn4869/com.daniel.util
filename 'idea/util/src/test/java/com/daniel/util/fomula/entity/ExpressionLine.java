package com.daniel.util.fomula.entity;

public class ExpressionLine implements Regexp {
  
  private final RegexpEnum regexpEnum = RegexpEnum.LINE;
  
  private String line;

  
  
  public ExpressionLine(String line) {
    this.line = line;
  }
  
  public String getLine() {
    return line;
  }
  
  public void setLine(String line) {
    this.line = line;
  }
  
  public RegexpEnum getRegexpEnum() {
    return regexpEnum;
  }
  
  public String toString() {
    return line.toString();
  }
  
}
