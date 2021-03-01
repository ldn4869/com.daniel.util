package com.daniel.util.fomula.entity;

import com.daniel.util.fomula.constant.BunchEnum;
import com.daniel.util.fomula.constant.CaptureEnum;
import com.daniel.util.fomula.constant.FormulaEnum;

/**
 * 捕获组
 *
 * 正则捕获组, 可以从材料中提取 name 标识的信息
 */
public class Capture implements Formula {

  private final FormulaEnum formulaEnum = FormulaEnum.CAPTURE;

  private String name;

  private BunchEnum bunchEnum;

  private CaptureEnum captureEnum;

  private Integer minRepeat = null;

  private Integer maxRepeat = null;

  public Capture() {
  }

  public Capture(String name) {
    this.name = name;
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

  public CaptureEnum getCaptureEnum() {
    return captureEnum;
  }

  public void setCaptureEnum(CaptureEnum captureEnum) {
    this.captureEnum = captureEnum;
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
