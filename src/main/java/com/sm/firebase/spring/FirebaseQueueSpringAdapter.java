package com.sm.firebase.spring;

import java.lang.reflect.Method;
import java.util.Map;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.sm.firebase.queue.MessageListener;

public class FirebaseQueueSpringAdapter implements MessageListener {
  private Method method;

  public FirebaseQueueSpringAdapter(Method method) {
    this.method = method;
  }

  @Override
  public void handle(DataSnapshot eventSnapshot) {
    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
    };
    Map<String, String> header = eventSnapshot.child("header")
        .getValue(genericTypeIndicator);
    System.out.println(">>> header: " + header);
    header.forEach((key, value) -> {
      System.out.println(">>>> " + key + " : " + value);
    });

    if (method.getParameterCount() == 1) {
      Class<?> parameterType = method.getParameterTypes()[0];
      System.out.println(">>> expected message type: " + parameterType);

      Object payload = eventSnapshot.child("payload").getValue(parameterType);
      System.out.println(">> payload: " + payload);
      System.out.println(">>>> is type of " + parameterType + "   "
          + parameterType.isInstance(payload));
      System.out.println(">>> message class: "+ payload.getClass());

      try {
        method.setAccessible(true);
        Object response = method.invoke(payload);

        String replyTo = String.valueOf(header.get("replyTo"));
        if (replyTo != null && replyTo.length() > 0) {
          reply(replyTo, response, eventSnapshot.getRef().getDatabase());
        }
      } catch (Exception e) {
        e.printStackTrace();
        // TODO: push error message to firebase with correlationId
        new IllegalStateException("Firebase queue message handler error");
      }
    } else {
      // TODO: push error message to firebase with correlationId
      throw new IllegalStateException(
          "Only one parameter is expected in message handler");
    }

  }

  private void reply(String replyTo, Object response, FirebaseDatabase db) {
    DatabaseReference ref = db.getReference(replyTo);
    ref.setValue(response);
  }

}
