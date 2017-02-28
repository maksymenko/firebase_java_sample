package com.sm.firebasequeue;

import com.google.firebase.database.DataSnapshot;

@FunctionalInterface
public interface EventListener {
  void handle(DataSnapshot eventSnapshot);
}
