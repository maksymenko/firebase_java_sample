package com.sm.firebasequeue;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class QueueExecutor {
  private static final int DEFAULT_THREAD_POOL_SIZE = 3;
  private static final long KEEP_ALIVE_TIME = 0;
  private final ThreadPoolExecutor executorPool;

  public QueueExecutor(String queueName) {
    this(queueName, DEFAULT_THREAD_POOL_SIZE);
  }

  public QueueExecutor(String queueName, int threadPoolSize) {
    executorPool = new ThreadPoolExecutor(threadPoolSize, threadPoolSize,
        KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>(threadPoolSize),
        new QueueThreadFactory(queueName),
        new ThreadPoolExecutor.CallerRunsPolicy());
  }

  public void execute(Runnable worker) {
    executorPool.execute(worker);
  }

  private static final class QueueThreadFactory implements ThreadFactory {
    private final String threadNamePrefix;
    private int threadCounter = 0;

    public QueueThreadFactory(String threadNamePrefix) {
      this.threadNamePrefix = threadNamePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
      Thread t = new Thread(r);
      t.setName(threadNamePrefix + "-th-" + threadCounter++);
      return t;
    }

  }
}
