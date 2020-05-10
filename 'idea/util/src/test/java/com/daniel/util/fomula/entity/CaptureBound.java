package com.daniel.util.fomula.entity;

public class CaptureBound implements Regexp, Indexable {

  private final RegexpEnum regexpEnum = RegexpEnum.CAPTURE_BOUND;

  private String name;

  private BunchEnum bunchEnum;

  private BoundEnum boundEnum;

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

  @Override
  public String toString() {
    return boundEnum == BoundEnum.OPEN ? "(?<" + name + ">" : ")";
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
