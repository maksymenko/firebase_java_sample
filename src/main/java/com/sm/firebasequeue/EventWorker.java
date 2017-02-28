package com.sm.firebasequeue;

import java.util.Map;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.Transaction.Handler;
import com.google.firebase.database.Transaction.Result;

public class EventWorker implements Runnable {
  private final MessageListener messageListener;
  private DataSnapshot eventSnapshot;

  public EventWorker(DataSnapshot eventSnapshot,
      MessageListener messageListener) {
    this.eventSnapshot = eventSnapshot;
    this.messageListener = messageListener;
  }

  @Override
  public void run() {
    eventSnapshot.getRef().runTransaction(new Handler() {
      @Override
      public Result doTransaction(MutableData event) {
        Message message = event.getValue(Message.class);
        if (message != null) {
          Map<String, String> header = message.getHeader();
          if (header == null
              || "new".equalsIgnoreCase(header.getOrDefault("state", "new"))) {
            header.put("state", "in_progress");
            event.setValue(message);
            return Transaction.success(event);
          }
        }
        return Transaction.abort();
      }

      @Override
      public void onComplete(DatabaseError error, boolean committed,
          DataSnapshot message) {
        if (committed && message.exists()) {
          if (messageListener != null) {
            messageListener.handle(message.getValue(Message.class));
          }
          message.getRef().removeValue();
        }
      }
    });
  }

  @Override
  public String toString() {
    return "MessageWorker [eventSnapshot=" + eventSnapshot + "]";
  }

}
