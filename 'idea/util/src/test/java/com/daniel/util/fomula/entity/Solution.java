package com.daniel.util.fomula.entity;

/**
 * 解式
 * <p>
 * 由公式和材料匹配得出的捕获组信息
 *
 * @author Daniel
 */
public class Solution {

  /**
   * 若 FormulaEnum 为 CAPTURE 且 BunchEnum 为 LIST, 说明其所在结点的子结点的解式集合 才是 逻辑上该结点的解式
   */
  private Formula formula;

  private String material;

  private String value;

  public Solution() {

  }

  public Solution(String material) {
    this.material = material;
  }

  public Solution(Formula formula) {
    this.formula = formula;
  }

  public Solution(Formula formula, String material) {
    this.formula = formula;
    this.material = material;
  }

  public Formula getFormula() {
    return formula;
  }

  public void setFormula(Formula formula) {
    this.formula = formula;
  }

  public String getMaterial() {
    return material;
  }

  public void setMaterial(String material) {
    this.material = material;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return formula.toString() + ": " + value;
  }
}
