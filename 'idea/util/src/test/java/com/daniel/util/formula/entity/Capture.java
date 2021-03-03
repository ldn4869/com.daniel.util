package com.daniel.util.formula.entity;

import com.daniel.util.formula.constant.BunchEnum;
import com.daniel.util.formula.constant.FormulaEnum;
import com.daniel.util.json.PojoUtil;

/**
 * 捕获组
 * <p>
 * 正则捕获组, 可以从材料中提取 name 标识的信息
 */
public class Capture implements Formula {

  private final FormulaEnum formulaEnum = FormulaEnum.CAPTURE;

  private String name;

  private BunchEnum bunchEnum;

  private Integer minRepeat = null;

  private Integer maxRepeat = null;

  public Capture() {
  }

  public Capture(String name) {
    this.name = name;
  }

  public Capture(String name, BunchEnum bunchEnum) {
    this.name = name;
    this.bunchEnum = bunchEnum;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BunchEnum getBunchEnum() {
    return bunchEnum;
  }

  public FormulaEnum getFormulaEnum() {
    return formulaEnum;
  }

  public void setBunchEnum(BunchEnum bunchEnum) {
    this.bunchEnum = bunchEnum;
  }

  public Integer getMinRepeat() {
    return minRepeat;
  }

  public void setMinRepeat(Integer minRepeat) {
    this.minRepeat = minRepeat;
  }

  public Integer getMaxRepeat() {
    return maxRepeat;
  }

  public void setMaxRepeat(Integer maxRepeat) {
    this.maxRepeat = maxRepeat;
  }

  @Override
  public Capture clone() {
    return PojoUtil.copy(this, Capture.class);
  }

  public String getRepeatSuffix() {
    if (BunchEnum.LIST == bunchEnum) {
      return "{"
          + (getMinRepeat() != null ? getMinRepeat() : 0) + ","
          + (getMaxRepeat() != null ? getMaxRepeat() : "") + "}";
    } else {
      return "";
    }
  }

  @Override
  public String toString() {
    return "?<" + name + ">" + getRepeatSuffix();

  }

}
