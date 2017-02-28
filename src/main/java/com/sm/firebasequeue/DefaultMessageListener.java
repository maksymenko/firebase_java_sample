package com.sm.firebasequeue;

public class DefaultMessageListener implements MessageListener {

  @Override
  public void handle(Message message) {
    System.out.println(">>> Incomming message received <<<");

    System.out.println(" header");
    message.getHeader().forEach((key, value) -> {
      System.out.println("  " + key + " : " + value);
    });

    System.out.println(" body");
    message.getBody().forEach((key, value) -> {
      System.out.println("  " + key + " : " + value);
    });
    System.out.println("============================");
  }

}
