package com.sm.firebasequeue;

import com.google.firebase.database.DataSnapshot;

/**
 * Internal interface to define handler for Firebase events.
 *
 */
interface EventListener {
  void handle(DataSnapshot eventSnapshot);
}
