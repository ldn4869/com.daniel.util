package com.daniel.util.formula.entity;

import com.daniel.util.formula.constant.FormulaEnum;
import com.daniel.util.json.PojoUtil;

/**
 * 行表达式
 * <p>
 * 代表匹配一行材料
 */
public class ExpressionLine implements Formula {

  private final FormulaEnum formulaEnum = FormulaEnum.LINE;

  private String line;

  public ExpressionLine() {

  }

  public ExpressionLine(String line) {
    this.line = line;
  }

  public String getLine() {
    return line;
  }

  public void setLine(String line) {
    this.line = line;
  }

  @Override
  public ExpressionLine clone() {
    return PojoUtil.copy(this, ExpressionLine.class);
  }

  public FormulaEnum getFormulaEnum() {
    return formulaEnum;
  }

  public String toString() {
    return line.toString();
  }

}
