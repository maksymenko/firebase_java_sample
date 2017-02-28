package com.sm.firebasequeue;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class EventHandler implements ChildEventListener {
  private EventListener eventListener;

  public EventHandler(EventListener messageListener) {
    this.eventListener = messageListener;
  }

  @Override
  public void onCancelled(DatabaseError error) {
  }

  @Override
  public void onChildAdded(DataSnapshot eventSnapshot,
      String previousChildKey) {
    eventListener.handle(eventSnapshot);
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

}
