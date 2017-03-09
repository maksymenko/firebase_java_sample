package com.sm.firebase.spring;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sm.firebase.queue.Message;
import com.sm.firebase.spring.BindingAnnotations.FirebaseQueueOnMessage;
import com.sm.firebase.spring.BindingAnnotations.FirebaseQueueSubscriber;

@FirebaseQueueSubscriber
public class FirebaseQueueMessageSubscriber {

  @FirebaseQueueOnMessage(queueName = "dto_queue")
  public String hello(MessageDto message) {
    System.out.println(">>>> dto " + message);
    return "dto: " + message;
  }

  @FirebaseQueueOnMessage(queueName = "string_queue")
  public String hello1(String message) {
    System.out.println(">>>> string " + message);
    return "string: " + message;
  }

  public void sendDebugMessage() {
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db
        .getReference("messageBrocker/queue_name/messages");

    Map<String, String> header = new HashMap<>();
    header.put("state", "new");
    header.put("replyTo", "response/123");
    String payloadContent = "test message string";
    Message message = new Message();
    message.setHeader(header);
    message.setPayload(payloadContent);
    ref.push().setValue(message);

  }

}
