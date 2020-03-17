package com.daniel.util.entity;

import java.util.Date;

public class StudentVo {

  private String name;
  private int age;
  private String sex;
  private Date birthday;



  public StudentVo() {
    super();
  }

  public StudentVo(String name, int age, String sex) {
    super();
    this.name = name;
    this.age = age;
    this.sex = sex;
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
  public String getSex() {
    return sex;
  }
  public void setSex(String sex) {
    this.sex = sex;
  }

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

  @Override
  public String toString() {
    return "Student [name=" + name + ", age=" + age + ", sex=" + sex + "]";
  }


}
