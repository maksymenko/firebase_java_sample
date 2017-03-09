package com.sm.firebase.spring;

import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import com.sm.firebase.queue.Queue;
import com.sm.firebase.spring.BindingAnnotations.FirebaseQueueOnMessage;
import com.sm.firebase.spring.BindingAnnotations.FirebaseQueueSubscriber;

@Component
public class FirebaseQueuePostProcessor implements BeanPostProcessor {
  private static final String FIREBASE_URL = "https://catalogsample-cafa7.firebaseio.com";
  private static final String FIREBASE_KEY_FILE_NAME = "service-account.json";
  private Queue queue = new Queue(FIREBASE_URL, FIREBASE_KEY_FILE_NAME);

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
      for (Method method : bean.getClass().getMethods()) {
        FirebaseQueueOnMessage onMessageHandler = AnnotationUtils
            .findAnnotation(method, FirebaseQueueOnMessage.class);
        if (onMessageHandler != null) {
          FirebaseQueueSpringAdapter adapter = new FirebaseQueueSpringAdapter(
              bean, method);
          String queueName = onMessageHandler.queueName();
          queue.listenQueue(queueName, adapter);
          System.out.println(">>> started queue: '" + queueName
              + "' subscriber bean '" + beanName + "'  handled by method: '"
              + method.getName() + "'");
        }
      }
    }
    return bean;
  }

}
