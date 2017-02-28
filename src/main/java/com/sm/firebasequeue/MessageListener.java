package com.sm.firebasequeue;

public interface MessageListener {
  void handle(Message message);
}
