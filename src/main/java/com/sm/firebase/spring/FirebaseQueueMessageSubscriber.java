package com.sm.firebase.spring;

import com.sm.firebase.spring.BindingAnnotations.FirebaseQueueOnMessage;
import com.sm.firebase.spring.BindingAnnotations.FirebaseQueueSubscriber;

@FirebaseQueueSubscriber
public class FirebaseQueueMessageSubscriber {

  @FirebaseQueueOnMessage(queueName = "queue_name")
  public void hello(String str) {
    System.out.println(">>>> say hello");
  }
}
