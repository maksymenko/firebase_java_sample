package com.sm.firebase.spring;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.joda.time.DateTime;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.sm.firebase.queue.Message;
import com.sm.firebase.queue.MessageListener;

public class FirebaseQueueSpringAdapter implements MessageListener {
  private Object bean;
  private Method method;

  // TODO: pass database ref in constructor, from queue impl.
  public FirebaseQueueSpringAdapter(Object bean, Method method) {
    this.method = method;
    this.bean = bean;
  }

  // TODO: use Message instead of snapshot
  @Override
  public void handle(DataSnapshot eventSnapshot) {
    System.out.println("Firebase queue messge: " + eventSnapshot);
    if (method.getParameterCount() == 1) {
      Class<?> parameterType = method.getParameterTypes()[0];
      try {
        Object payload = eventSnapshot.child("payload").getValue(parameterType);
        method.setAccessible(true);
        Object response = method.invoke(bean, payload);
        reply(eventSnapshot, "OK", response);
      } catch (Exception e) {
        reply(eventSnapshot, "error", ExceptionUtils.getRootCauseMessage(e));
        e.printStackTrace();
        new IllegalStateException("Firebase queue message handler error");
      }
    } else {
      throw new IllegalStateException("Only one parameter is expected in message handler");
    }
  }

  private static void reply(DataSnapshot eventSnapshot, String status, Object responseMsg) {
    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
    };
    Map<String, String> header = eventSnapshot.child("header").getValue(genericTypeIndicator);
    String replyTo = header.get("replyTo");
    if (replyTo != null && replyTo.length() > 0) {
      DatabaseReference dbRef = eventSnapshot.getRef().getDatabase().getReference(replyTo);
      Message response = new Message();
      response.addHeaderParam("status", status);
      response.addHeaderParam("creationDate", Long.toString(System.currentTimeMillis()));
      response.setPayload(responseMsg);
      dbRef.setValue(response);
    }
  }
}
