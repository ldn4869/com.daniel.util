package com.daniel.util.fomula.entity;

public class CaptureBound implements Regexp, Indexable {

  private final RegexpEnum regexpEnum = RegexpEnum.CAPTURE_BOUND;

  private String name;

  private BunchEnum bunchEnum;

  private BoundEnum boundEnum;

  private Integer minRepeat = null;

  private Integer maxRepeat = null;

  private int start;

  private int end;

  @Override
  public RegexpEnum getRegexpEnum() {
    return regexpEnum;
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

  public BoundEnum getBoundEnum() {
    return boundEnum;
  }

  public void setBoundEnum(BoundEnum boundEnum) {
    this.boundEnum = boundEnum;
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
    return boundEnum == BoundEnum.OPEN ? "(?<" + name + ">" : bunchEnum == BunchEnum.ELEMENT ? ")" : ")+";
  }

  @Override
  public int getStart() {
    return start;
  }

  @Override
  public void setStart(int start) {
    this.start = start;
  }

  @Override
  public int getEnd() {
    return end;
  }

  @Override
  public void setEnd(int end) {
    this.end = end;
  }
}
