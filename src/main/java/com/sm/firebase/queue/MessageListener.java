package com.sm.firebase.queue;

import com.google.firebase.database.DataSnapshot;

/**
 * Interface for external message listeners. Is executed in separate thread to
 * handle incoming message.
 *
 */
public interface MessageListener {
  void handle(DataSnapshot eventSnapshot);
}
