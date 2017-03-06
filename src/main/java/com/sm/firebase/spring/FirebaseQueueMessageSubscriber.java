package com.sm.firebase.spring;

@ FirebaseQueueSubscriber(queueName = "queue_name")
public class FirebaseQueueMessageSubscriber {
  public void hello(){
    System.out.println(">>>> say hello");
  }
}
