package com.sm.firebase.queue;

import com.google.firebase.database.DataSnapshot;

public class DefaultMessageListener implements MessageListener {

  @Override
  public void handle(DataSnapshot eventSnapshot) {
    Message message = eventSnapshot.getValue(Message.class);
    System.out.println("########################");
    System.out.println(">>> Incomming message received in thread "
        + Thread.currentThread().getName() + " <<<");

    System.out.println(" header");
    message.getHeader().forEach((key, value) -> {
      System.out.println("  " + key + " : " + value);
    });
    System.out.println(" payload: " + message.getPayload());
    System.out.println("============================");
  }

}
