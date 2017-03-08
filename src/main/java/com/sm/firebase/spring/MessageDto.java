package com.sm.firebase.spring;

public class MessageDto {
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "MessageDto [name=" + name + "]";
  }

}
