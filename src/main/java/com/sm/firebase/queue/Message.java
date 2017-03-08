package com.sm.firebase.queue;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents Message enclosed in incoming Firebase event.
 *
 */
public class Message {
  private Map<String, String> header = new HashMap<>();
  private Object payload = new Object();

  public Map<String, String> getHeader() {
    return header;
  }

  public void setHeader(Map<String, String> header) {
    this.header = header;
  }

  public Object getPayload() {
    return payload;
  }

  public void setPayload(Object payload) {
    this.payload = payload;
  }

  @Override
  public String toString() {
    return "Message [header=" + header + ", payload=" + payload + "]";
  }

}
