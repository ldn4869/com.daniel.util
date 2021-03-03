package com.daniel.util.formula.entity;

/**
 * 解式
 * <p>
 * 由公式和材料匹配得出的捕获组信息
 *
 * @author Daniel
 */
public class Solution implements Cloneable {

  /**
   * 若 FormulaEnum 为 CAPTURE 且 BunchEnum 为 LIST, 说明其所在结点的子结点的解式集合 才是 逻辑上该结点的解式
   */
  private Formula formula;

  private String value;

  public Solution() {

  }

  public Solution(Formula formula) {
    this.formula = formula;
  }

  public Solution(Formula formula, String value) {
    this.formula = formula;
    this.value = value;
  }

  public Solution(String value) {
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

  public Solution clone() {
    Solution solution = new Solution();
    solution.setFormula(formula.clone());
    solution.setValue(value);
    return solution;
  }

  @Override
  public String toString() {
    return (formula != null ? formula.toString() : "?<>") + ": " + value;
  }
}
