package com.daniel.util.fomula.entity;

public class Solution {

  private Formula formula;

  private String value;

  public Solution(Formula formula) {
    this.formula = formula;
  }

  public Solution(Formula formula, String value) {
    this.formula = formula;
    this.value = value;
  }

  public Formula getFormula() {
    return formula;
  }

  public void setFormula(Formula formula) {
    this.formula = formula;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
