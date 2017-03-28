package com.sm.firebase.queue;

import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;

/**
 * Firebase event worker. Is executed in separate thread, passes message
 * (extracted from event) to external listener, then removes snapshot in
 * Firebase.
 */
class MessageWorker implements Runnable {

  private final MessageListener messageListener;
  private DataSnapshot eventSnapshot;

  /**
   * Creates worker for given Firebase event.
   * 
   * @param eventSnapshot Firebase child event.
   * @param messageListener external listener of incoming message.
   */
  public MessageWorker(DataSnapshot eventSnapshot, MessageListener messageListener) {
    this.eventSnapshot = eventSnapshot;
    this.messageListener = messageListener;
  }

  @Override
  public void run() {
    if (messageListener != null) {
      try {
        Object response = messageListener.handle(eventSnapshot);
        reply(eventSnapshot, "OK", response);
      } catch (Exception e) {
        reply(eventSnapshot, "error", ExceptionUtils.getRootCauseMessage(e));
        e.printStackTrace();
      }

    }
    eventSnapshot.getRef().removeValue();
  }

  private static void reply(DataSnapshot eventSnapshot, String status, Object responseMsg) {
    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
    };
    Map<String, String> header = eventSnapshot.child("header").getValue(genericTypeIndicator);
    String replyTo = header.get("replyTo");
    if (replyTo != null && replyTo.length() > 0) {
      DatabaseReference dbRef = eventSnapshot.getRef().getDatabase().getReference(replyTo);
      Message response = new Message();
      response.addHeaderParam("status", status);
      response.addHeaderParam("creationDate", Long.toString(System.currentTimeMillis()));
      response.setPayload(responseMsg);
      dbRef.setValue(response);
    }
  }
}
