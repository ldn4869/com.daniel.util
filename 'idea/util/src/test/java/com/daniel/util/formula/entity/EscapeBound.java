package com.daniel.util.formula.entity;

import com.daniel.util.formula.constant.BoundEnum;
import com.daniel.util.formula.constant.RegexpEnum;

public class EscapeBound implements Regexp, Indexable {

  private final RegexpEnum regexpEnum = RegexpEnum.ESCAPE_BOUND;

  private BoundEnum boundEnum;

  private int start;

  private int end;

  @Override
  public RegexpEnum getRegexpEnum() {
    return regexpEnum;
  }

  public BoundEnum getBoundEnum() {
    return boundEnum;
  }

  public void setBoundEnum(BoundEnum boundEnum) {
    this.boundEnum = boundEnum;
  }

  @Override
  public String toString() {
    return boundEnum == BoundEnum.OPEN ? "[" : "]";
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
