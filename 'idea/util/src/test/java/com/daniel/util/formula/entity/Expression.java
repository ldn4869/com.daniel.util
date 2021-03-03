package com.daniel.util.formula.entity;

import com.daniel.util.formula.constant.BoundEnum;
import com.daniel.util.formula.constant.BunchEnum;
import com.daniel.util.formula.constant.FormulaEnum;
import com.daniel.util.formula.constant.RegexpEnum;
import com.daniel.util.json.PojoUtil;
import org.apache.commons.lang3.StringUtils;

public class Expression implements Regexp, Indexable, Formula {
  
  private final RegexpEnum regexpEnum = RegexpEnum.EXPRESSION;

  private final FormulaEnum formulaEnum = FormulaEnum.EXPRESSION;
  
  private String content;
  
  private String regex;
  
  private BunchEnum bunchEnum;
  
  private BoundEnum boundEnum;

  private int start;

  private int end;

  public Expression() {

  }
  
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
  public Expression clone() {
    return PojoUtil.copy(this, Expression.class);
  }

  @Override
  public String toString() {
    return regex;
  }

  @Override
  public FormulaEnum getFormulaEnum() {
    return formulaEnum;
  }
}
