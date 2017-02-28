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
class EventWorker implements Runnable {
  private static final String STATE_FIELD_NAME = "state";
  private static final String STATE_NEW = "new";
  private static final String STATE_IN_PROGRESS = "in_progress";

  private final MessageListener messageListener;
  private DataSnapshot eventSnapshot;

  /**
   * Creates worker for given Firebase event.
   * 
   * @param eventSnapshot Firebase child event.
   * @param messageListener external listener of incoming message.
   */
  public EventWorker(DataSnapshot eventSnapshot,
      MessageListener messageListener) {
    this.eventSnapshot = eventSnapshot;
    this.messageListener = messageListener;
  }

  @Override
  public void run() {
    eventSnapshot.getRef().runTransaction(new Handler() {
      /**
       * Checks if event is in expected state and updates state.
       */
      @Override
      public Result doTransaction(MutableData event) {
        Message message = event.getValue(Message.class);
        if (message != null) {
          Map<String, String> header = message.getHeader();
          if (header == null || STATE_NEW.equalsIgnoreCase(
              header.getOrDefault(STATE_FIELD_NAME, STATE_NEW))) {
            header.put(STATE_FIELD_NAME, STATE_IN_PROGRESS);
            event.setValue(message);
            return Transaction.success(event);
          }
        }
        return Transaction.abort();
      }

      /**
       * Handles event if state is updated successfully in transaction.
       */
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
