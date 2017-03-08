package com.sm.firebase.spring;

import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import com.sm.firebase.spring.BindingAnnotations.FirebaseQueueOnMessage;
import com.sm.firebase.spring.BindingAnnotations.FirebaseQueueSubscriber;

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

      System.out
          .println(">> Found subscriber. bean:" + beanName + " class: " + bean);

      for (Method method : bean.getClass().getMethods()) {

        FirebaseQueueOnMessage onMessageHandler = AnnotationUtils
            .findAnnotation(method, FirebaseQueueOnMessage.class);
        if (onMessageHandler != null) {
          System.out.println(">>> handler for queue: " + onMessageHandler.queueName());
          System.out.println(">>>> methodName: " + method.getName());
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
