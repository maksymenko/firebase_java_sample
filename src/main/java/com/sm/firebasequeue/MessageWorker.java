package com.sm.firebasequeue;

import java.util.Map;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.Transaction.Handler;
import com.google.firebase.database.Transaction.Result;

/**
 * Firebase event worker. Is executed in separate thread, transactionally sets
 * status to "in-progress" and passes message (extracted from event) to external
 * listener, then removes event in Firebase.
 *
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
  public MessageWorker(DataSnapshot eventSnapshot,
      MessageListener messageListener) {
    this.eventSnapshot = eventSnapshot;
    this.messageListener = messageListener;
  }

  @Override
  public void run() {
    System.out.println(">>> message " + eventSnapshot + " processed in thread");
    if (messageListener != null) {
      messageListener.handle(eventSnapshot.getValue(Message.class));
    }
    System.out.println(">>>> remove event " + eventSnapshot);
    eventSnapshot.getRef().removeValue();

  }

  @Override
  public String toString() {
    return "MessageWorker [eventSnapshot=" + eventSnapshot + "]";
  }

}
