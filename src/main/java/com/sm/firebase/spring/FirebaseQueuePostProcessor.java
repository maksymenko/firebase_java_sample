package com.sm.firebase.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

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
    }
    return bean;
  }

}
