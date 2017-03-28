package com.sm.firebase.spring;

import java.lang.reflect.Method;

import com.google.firebase.database.DataSnapshot;
import com.sm.firebase.queue.MessageListener;

public class FirebaseQueueSpringAdapter implements MessageListener {
  private Object bean;
  private Method method;

  public FirebaseQueueSpringAdapter(Object bean, Method method) {
    this.method = method;
    this.bean = bean;
  }

  // TODO: use Message instead of snapshot, or use Jackson to convert Object to
  // actual parameter type. To avoid dependency on firebase in spring adapter.
  @Override
  public Object handle(DataSnapshot eventSnapshot) {
    System.out.println("Firebase queue messge: " + eventSnapshot);
    if (method.getParameterCount() == 1) {
      Class<?> parameterType = method.getParameterTypes()[0];
      try {
        Object payload = eventSnapshot.child("payload").getValue(parameterType);
        method.setAccessible(true);
        Object response = method.invoke(bean, payload);
        return response;
      } catch (Exception e) {
        e.printStackTrace();
        new IllegalStateException("Firebase queue message handler error", e);
      }
    }
    throw new IllegalStateException("Only one parameter is expected in message handler");
  }
}
