package com.sm.firebasequeue;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Firebase event handler. listen for new events and passes to listener.
 *
 */
class EventHandler implements ChildEventListener {
  //External event listener
  private EventListener eventListener;

  /**
   * Handler for child Firebase events.
   * @param eventListener
   */
  EventHandler(EventListener eventListener) {
    this.eventListener = eventListener;
  }

  @Override
  public void onCancelled(DatabaseError error) {
  }

  @Override
  public void onChildAdded(DataSnapshot eventSnapshot,
      String previousChildKey) {
    if (eventListener != null) {
      eventListener.handle(eventSnapshot);
    }
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
