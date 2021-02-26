package com.daniel.util.fomula.entity;

import com.daniel.util.fomula.constant.BoundEnum;
import com.daniel.util.fomula.constant.RegexpEnum;

public class LineBound implements Regexp {
  
  private final RegexpEnum regexpEnum = RegexpEnum.LINE_BOUND;
  
  private BoundEnum boundEnum;

  public LineBound() {
  }

  public LineBound(BoundEnum boundEnum) {
    this.boundEnum = boundEnum;
  }
  
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
    return BoundEnum.OPEN == boundEnum ? "^" : "$";
  }
}
