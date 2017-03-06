package com.sm.firebase.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

@Configuration
@ComponentScan("com.sm.firebase.spring")
public class Starter {
  public static void main(String[] args) {
    System.out.println(">>>");

    try (
        AbstractApplicationContext app = new AnnotationConfigApplicationContext(
            Starter.class)) {
//
//      System.out.println(">>> app: " + app);
//      for (String beanName : app.getBeanDefinitionNames()) {
//        System.out.println(">>> " + beanName);
//      }
//
//      FirebaseQueueMessageSubscriber subscriber = app
//          .getBean(FirebaseQueueMessageSubscriber.class);
//      subscriber.hello();

    }
  }
}
