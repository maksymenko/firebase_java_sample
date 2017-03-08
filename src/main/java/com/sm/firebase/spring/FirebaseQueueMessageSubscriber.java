package com.sm.firebase.spring;

import com.sm.firebase.spring.BindingAnnotations.FirebaseQueueOnMessage;
import com.sm.firebase.spring.BindingAnnotations.FirebaseQueueSubscriber;

@FirebaseQueueSubscriber
public class FirebaseQueueMessageSubscriber {

  @FirebaseQueueOnMessage(queueName = "queue_name")
  public String hello(MessageDto message) {
    System.out.println(">>>> say hello " + message);
    return "hello: " + message;
  }
}
