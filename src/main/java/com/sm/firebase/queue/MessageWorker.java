package com.sm.firebase.queue;

import com.google.firebase.database.DataSnapshot;

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
  public MessageWorker(DataSnapshot eventSnapshot,
      MessageListener messageListener) {
    this.eventSnapshot = eventSnapshot;
    this.messageListener = messageListener;
  }

  @Override
  public void run() {
    if (messageListener != null) {
      messageListener.handle(eventSnapshot);
    }
    eventSnapshot.getRef().removeValue();
  }
}
