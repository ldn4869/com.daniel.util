package com.daniel.util.entity;

import java.util.Date;

public class StudentVo {

  private String name;
  private int age;
//  private String sex;
  private Date birthday;



  public StudentVo() {
    super();
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public int getAge() {
    return age;
  }
  
  public void setAge(int age) {
    this.age = age;
  }
  
  public Date getBirthday() {
    return birthday;
  }
  
  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }
}
