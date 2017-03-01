package com.sm.firebasequeue;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.Transaction.Handler;
import com.google.firebase.database.Transaction.Result;

/**
 * Firebase event handler. Listen for new events and claims message from queue
 * to process.
 *
 */
class MessageClaimer implements ChildEventListener {
  private static final String STATE_FIELD_NAME = "state";
  private static final String STATE_NEW = "new";
  private static final String STATE_IN_PROGRESS = "in_progress";

  // External event listener
  private final MessageListener messageListener;
  // Multithreaded executor.
  private final QueueExecutor queueExecutor;

  /**
   * Handler for child Firebase events.
   * 
   * @param eventListener
   */
  MessageClaimer(QueueExecutor queueExecutor, MessageListener messageListener) {
    if (queueExecutor == null || messageListener == null) {
      throw new IllegalArgumentException("Parametera can't be null");
    }
    this.queueExecutor = queueExecutor;
    this.messageListener = messageListener;
  }

  @Override
  public void onCancelled(DatabaseError error) {
  }

  @Override
  public void onChildAdded(DataSnapshot eventSnapshot,
      String previousChildKey) {
    System.out.println(">>> child added " + eventSnapshot);
    claimMessage(eventSnapshot);
  }

  @Override
  public void onChildChanged(DataSnapshot eventSnapshot,
      String previousChildKey) {
  }

  @Override
  public void onChildMoved(DataSnapshot eventSnapshot,
      String previousChildKey) {
  }

  @Override
  public void onChildRemoved(DataSnapshot eventSnapshot) {
  }

  private void claimMessage(DataSnapshot eventSnapshot) {
    eventSnapshot.getRef().runTransaction(new Handler() {
      private boolean valid = false;

      @Override
      public Result doTransaction(MutableData event) {
        System.out.println(">>> do in transaction event: " + event);
        try {
          Message message = event.getValue(Message.class);
          valid = true;
          Map<String, String> header = message.getHeader();
          header.put(STATE_FIELD_NAME, STATE_IN_PROGRESS);
          event.setValue(message);
          System.out.println(">>> try claim message " + message);
        } catch (Exception e) {
          e.printStackTrace();
          Map<String, Object> error = new HashMap<>();
          error.put("error", "invalid message");
          error.put("originalKey", eventSnapshot.getKey());
          error.put("originalMessage", event.getValue());
          event.setValue(error);
        }
        return Transaction.success(event);
      }

      /**
       * Handles event if state is updated successfully in transaction.
       */
      @Override
      public void onComplete(DatabaseError error, boolean committed,
          DataSnapshot message) {
        System.out.println(">>> oncomplete  event: " + message + " committed: "
            + committed + " valid: " + valid);

        if (committed && message.exists()) {
          if (valid) {
            System.out.println(">>> message claimed: " + message);
            queueExecutor.execute(new MessageWorker(message, messageListener));
          } else {
            System.out.println(">>> move invalid message: " + message);
            DatabaseReference queueRootRef = eventSnapshot.getRef().getParent()
                .getParent();
            DatabaseReference invalidRef = queueRootRef.child("invalid").push();
            invalidRef.setValue(message.getValue());
            message.getRef().removeValue();
          }
        }
      }
    });
  }

}
