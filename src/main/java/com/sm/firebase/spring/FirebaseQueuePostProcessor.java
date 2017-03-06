package com.sm.firebase.spring;

import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import com.sm.firebase.spring.interfaces.FirebaseQueueMessageHandler;
import com.sm.firebase.spring.interfaces.FirebaseQueueSubscriber;

@Component
public class FirebaseQueuePostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {

    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName)
      throws BeansException {

    FirebaseQueueSubscriber firebaseQueueSubscriber = AnnotationUtils
        .findAnnotation(bean.getClass(), FirebaseQueueSubscriber.class);

    if (firebaseQueueSubscriber != null) {

      System.out.println(">> Found sibscriber for queue: \""
          + firebaseQueueSubscriber.queueName() + "\" beanName: " + beanName
          + " class: " + bean);

      for (Method method : bean.getClass().getMethods()) {

        FirebaseQueueMessageHandler messageHandler = AnnotationUtils
            .findAnnotation(method, FirebaseQueueMessageHandler.class);
        if (messageHandler != null) {
          System.out.println(">>> method to handle: " + method.getName());
          System.out.println(">>>> queue: " + messageHandler.queueName());
          System.out.println(">>>> messageName: " + messageHandler.messageName());
          Class<?>[] parameterTypes = method.getParameterTypes();
          for (Class<?> parameterType : parameterTypes) {
            System.out.println(">>>> parameterType: " + parameterType);
          }
        }
      }
    }

    return bean;
  }

}
