package com.sm.firebase.queue;

import com.google.firebase.database.DataSnapshot;

/**
 * Interface for external message listeners. Is executed in separate thread to
 * handle incoming message.
 *
 */
// TODO: use Message class instead of snapshot, to avoid firebase specific
// classes in external listeners.
public interface MessageListener {
  Object handle(DataSnapshot eventSnapshot);
}
