package com.sm.firebasequeue;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents Message enclosed in incoming Firebase event.
 *
 */
public class Message {
  private Map<String, String> header = new HashMap<>();
  private Map<String, String> body = new HashMap<>();

  public Map<String, String> getHeader() {
    return header;
  }

  public void setHeader(Map<String, String> header) {
    this.header = header;
  }

  public Map<String, String> getBody() {
    return body;
  }

  public void setBody(Map<String, String> body) {
    this.body = body;
  }
  
  public void addHeader(String key, String value) {
    header.put(key, value);
  }

  public void addBody(String key, String value) {
    body.put(key, value);
  }
}
