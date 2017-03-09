package com.sm.firebase.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

@Configuration
@ComponentScan("com.sm.firebase.spring")  
public class Starter {
  public static void main(String[] args) {
    System.out.println(">>>Firebase Message broker server started");

    try (
        AbstractApplicationContext app = new AnnotationConfigApplicationContext(
            Starter.class)) {
      FirebaseQueueMessageSubscriber bean = app
          .getBean(FirebaseQueueMessageSubscriber.class);
      bean.sendDebugMessage();
      Thread.currentThread().join();

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
