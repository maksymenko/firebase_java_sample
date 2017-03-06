package com.sm.firebase.spring;

import com.sm.firebase.spring.interfaces.FirebaseQueueMessageHandler;
import com.sm.firebase.spring.interfaces.FirebaseQueueSubscriber;

@ FirebaseQueueSubscriber(queueName = "queue_name")
public class FirebaseQueueMessageSubscriber {
  
  @FirebaseQueueMessageHandler
  public void hello(String str){
    System.out.println(">>>> say hello");
  }
}
