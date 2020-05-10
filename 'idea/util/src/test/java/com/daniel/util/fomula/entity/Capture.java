package com.daniel.util.fomula.entity;

public class Capture implements Regexp {
  
  private final RegexpEnum regexpEnum = RegexpEnum.CAPTURE;
  
  private String name;
  
  private BunchEnum bunchEnum;
  
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
  
  @Override
  public String toString() {
    if (BunchEnum.LIST == bunchEnum) {
      return "?<" + name + ">+";
    } else {
      return "?<" + name + ">";
    }
    
  }
  
}
