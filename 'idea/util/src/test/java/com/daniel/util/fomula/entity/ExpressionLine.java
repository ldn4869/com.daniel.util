package com.daniel.util.fomula.entity;

import com.daniel.util.fomula.constant.FormulaEnum;

public class ExpressionLine implements Formula {
  
  private final FormulaEnum formulaEnum = FormulaEnum.LINE;
  
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
  
  public FormulaEnum getFormulaEnum() {
    return formulaEnum;
  }
  
  public String toString() {
    return line.toString();
  }
  
}
