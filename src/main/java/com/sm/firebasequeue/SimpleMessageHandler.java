package com.sm.firebasequeue;

public class SimpleMessageHandler implements MessageListener {

  @Override
  public void handle(Message message) {
    System.out.println(">>> handle message <<<");

    System.out.println(" header");
    message.getHeader().forEach((key, value) -> {
      System.out.println("  " + key + " : " + value);
    });

    System.out.println(" body");
    message.getBody().forEach((key, value) -> {
      System.out.println("  " + key + " : " + value);
    });
    System.out.println("======================");
  }

}
