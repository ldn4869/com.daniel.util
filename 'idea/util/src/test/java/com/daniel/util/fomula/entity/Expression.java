package com.daniel.util.fomula.entity;

import org.apache.commons.lang3.StringUtils;

public class Expression implements Regexp, Indexable {
  
  private final RegexpEnum regexpEnum = RegexpEnum.EXPRESSION;
  
  private String content;
  
  private String regex;
  
  private BunchEnum bunchEnum;
  
  private BoundEnum boundEnum;

  private int start;

  private int end;
  
  public Expression(String regex) {
    this.regex = regex;
  }

  public Expression(String regex, int start, int end) {
    this.regex = StringUtils.substring(regex, start, end);
    this.start = start;
    this.end = end;
  }

  @Override
  public RegexpEnum getRegexpEnum() {
    return regexpEnum;
  }
  
  public String getContent() {
    return content;
  }
  
  public void setContent(String content) {
    this.content = content;
  }
  
  public String getRegex() {
    return regex;
  }
  
  public void setRegex(String regex) {
    this.regex = regex;
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

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getEnd() {
    return end;
  }

  public void setEnd(int end) {
    this.end = end;
  }

  @Override
  public String toString() {
    return regex;
  }
}
