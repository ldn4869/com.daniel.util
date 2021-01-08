package com.daniel.util.fomula.entity;

public class Capture implements Regexp {

  private final RegexpEnum regexpEnum = RegexpEnum.CAPTURE;

  private String name;

  private BunchEnum bunchEnum;

  private Integer minRepeat = null;

  private Integer maxRepeat = null;

  public Capture() {
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

  public void setBunchEnum(BunchEnum bunchEnum) {
    this.bunchEnum = bunchEnum;
  }

  public RegexpEnum getRegexpEnum() {
    return regexpEnum;
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
  public String toString() {
    if (BunchEnum.LIST == bunchEnum) {
      return "?<" + name + ">{"
          + (getMinRepeat() != null ? getMinRepeat() : "") + ","
          + (getMaxRepeat() != null ? getMaxRepeat() : "") + "}";
    } else {
      return "?<" + name + ">";
    }

  }

}
