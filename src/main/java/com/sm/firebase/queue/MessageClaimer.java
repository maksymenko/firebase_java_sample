package com.sm.firebase.queue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
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
  private static final String HEADER_STATE_KEY = "header/" + STATE_FIELD_NAME;
  private static final String QUEUE_MESSAGE_PATH = "messages";
  private static final String QUEUE_ERROR_PATH = "invalid";
  private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(
      "yyyy-MMM-dd HH:mm:ss Z");

  // External message handler
  private final MessageListener messageListener;
  // Multithreaded executor.
  private final QueueExecutor queueExecutor;
  // Firebase reference to queue
  private final DatabaseReference queueRef;

  public MessageClaimer(QueueExecutor queueExecutor, MessageListener messageListener,
      DatabaseReference queueRef) {
    if (queueExecutor == null || messageListener == null || queueRef == null) {
      throw new IllegalArgumentException("Parameters can't be null");
    }
    this.queueExecutor = queueExecutor;
    this.messageListener = messageListener;
    this.queueRef = queueRef;

  }

  /**
   * Creates and starts listener for given path (queueRef) in firebase realtime
   * database.
   */
  public void start() {
    DatabaseReference queueMessagesRef = queueRef.child(QUEUE_MESSAGE_PATH);
    Query newMessageQuery = queueMessagesRef.orderByChild(HEADER_STATE_KEY).equalTo(STATE_NEW)
        .limitToFirst(1);
    newMessageQuery.addChildEventListener(this);

    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss Z");
    queueRef.push().setValue("Listener started at: " + dateFormatter.format(new Date()));
  }

  @Override
  public void onCancelled(DatabaseError error) {
  }

  @Override
  public void onChildAdded(DataSnapshot eventSnapshot, String previousChildKey) {
    claimMessage(eventSnapshot);
  }

  @Override
  public void onChildChanged(DataSnapshot eventSnapshot, String previousChildKey) {
  }

  @Override
  public void onChildMoved(DataSnapshot eventSnapshot, String previousChildKey) {
  }

  @Override
  public void onChildRemoved(DataSnapshot eventSnapshot) {
  }

  /**
   * Handles new message in queue.
   */
  private void claimMessage(DataSnapshot eventSnapshot) {
    eventSnapshot.getRef().runTransaction(new Handler() {
      private boolean valid = false;

      @Override
      public Result doTransaction(MutableData event) {
        try {
          Message message = event.getValue(Message.class);
          valid = true;
          Map<String, String> header = message.getHeader();
          header.put(STATE_FIELD_NAME, STATE_IN_PROGRESS);
          header.put("last_attempt", dateFormatter.format(new Date()));
          String retryCntStr = header.getOrDefault("retry_cnt", "0");
          int retryCnt = Integer.parseInt(retryCntStr);
          header.put("retry_cnt", Integer.toString(++retryCnt));
          event.setValue(message);
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
       * Calls message handler and passes received message if state is updated
       * successfully in transaction. Ensures that only one listener receives
       * message.
       */
      @Override
      public void onComplete(DatabaseError error, boolean committed, DataSnapshot message) {

        if (committed && message.exists()) {
          if (valid) {
            queueExecutor.execute(new MessageWorker(message, messageListener));
          } else {
            DatabaseReference invalidRef = queueRef.child(QUEUE_ERROR_PATH).push();
            invalidRef.setValue(message.getValue());
            message.getRef().removeValue();
          }
        }
      }
    });
  }

}
