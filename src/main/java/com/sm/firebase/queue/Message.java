package com.sm.firebase.queue;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents Message enclosed in incoming Firebase event.
 *
 */
public class Message {
  private Map<String, String> header = new HashMap<>();
  private Map<String, String> payload = new HashMap<>();

  public Map<String, String> getHeader() {
    return header;
  }

  public void setHeader(Map<String, String> header) {
    this.header = header;
  }

  public Map<String, String> getPayload() {
    return payload;
  }

  public void setBody(Map<String, String> payload) {
    this.payload = payload;
  }

  public void addHeader(String key, String value) {
    header.put(key, value);
  }

  public void addBody(String key, String value) {
    payload.put(key, value);
  }

  @Override
  public String toString() {
    return "Message [header=" + header + ", payload=" + payload + "]";
  }

}
