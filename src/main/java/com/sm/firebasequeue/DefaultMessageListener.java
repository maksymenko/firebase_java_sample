package com.sm.firebasequeue;

public class DefaultMessageListener implements MessageListener {

  @Override
  public void handle(Message message) {
    System.out.println("########################");
    System.out.println(">>> Incomming message received in thread "
        + Thread.currentThread().getName() + " <<<");

    System.out.println(" header");
    message.getHeader().forEach((key, value) -> {
      System.out.println("  " + key + " : " + value);
    });

    System.out.println(" body");
    message.getBody().forEach((key, value) -> {
      System.out.println("  " + key + " : " + value);
    });
    System.out.println("============================");

    String threadToError = message.getBody().getOrDefault("error", "noThread");
    if (Thread.currentThread().getName().contains(threadToError)) {
      throw new RuntimeException("Error in worker");
    }

    String loopThread = message.getBody().getOrDefault("loop", "noThread");
    if (Thread.currentThread().getName().contains(loopThread)) {
      for (int i = 0; i < 100; i++) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " " + i);
      }
    }
  }

}
