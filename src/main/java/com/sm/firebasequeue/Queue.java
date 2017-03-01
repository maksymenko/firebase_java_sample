package com.sm.firebasequeue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Provides implementation of messaging queue based on Firebase.
 */
public class Queue {
  private static final String DEFAULT_QUEUE_PATH = "queue";
  private static final String FIREBASE_URL = "https://catalogsample-cafa7.firebaseio.com";
  private static final String FIREBASE_KEY_FILE_NAME = "service-account.json";

  private String dbUrl;
  private String serviceAccountFileName;
  private String queuePath = DEFAULT_QUEUE_PATH;
  // External handler (subscriber) which listen and handles incoming messages.
  private MessageListener messageListener = new DefaultMessageListener();

  /**
   * Runner method is used in when queue executed as standalone application.
   * Method starts queue server, then main thread is blocked until application
   * will be terminated.
   */
  public static void main(String[] args) throws InterruptedException {
    System.out.println(">>> queue starting...");

    Queue.db(FIREBASE_URL, FIREBASE_KEY_FILE_NAME).queuePath("queue").start(1);

    Thread.currentThread().join();
  }

  /**
   * Main builder method. Creates queue instance based on Firebase parameters..
   *
   * @param dbUrl Firebase URL.
   * @param serviceAccountFileName file contains private key to firebase
   *          project.
   * @return initiated instance of queue.
   */
  public static Queue db(String dbUrl, String serviceAccountFileName) {
    Queue queue = new Queue();
    queue.dbUrl = dbUrl;
    queue.serviceAccountFileName = serviceAccountFileName;
    return queue;
  }

  /**
   * Assigns external message listener for incoming messages.
   * 
   * @param messageListener
   * @return configured instance of queue.
   */
  public Queue messageHandler(MessageListener messageListener) {
    this.messageListener = messageListener;
    return this;
  }

  /**
   * Defines path in Firebase database where incoming messages are expected.
   *
   * @param queuePath
   * @return
   */
  public Queue queuePath(String queuePath) {
    this.queuePath = queuePath;
    return this;
  }

  /**
   * Starts listen incoming messages, with default thread pool size.
   */
  public void start() {
    startListener(new QueueExecutor(queuePath));
  }

  /**
   * Starts listen incoming messages.
   *
   * @param threadPoolSize number of threads to handle incoming messages.
   */
  public void start(int threadPoolSize) {
    startListener(new QueueExecutor(queuePath, threadPoolSize));
  }

  /**
   * Starts listen incoming messages queried by predefined query.
   *
   * @param queueExecutor multithreaded worker executor.
   */
  private void startListener(QueueExecutor queueExecutor) {
    DatabaseReference queueRef = initDb();

    Query newMessageQuery = queueRef.orderByChild("header/state").equalTo("new")
        .limitToFirst(1);

    MessageClaimer eventHandler = new MessageClaimer(queueExecutor,
        messageListener);
    System.out
        .println(">>> waiting for message with \"header/state = new\" in path: "
            + queuePath + "/events");

    newMessageQuery.addChildEventListener(eventHandler);
  }

  /**
   * Firebase database initializer.
   * 
   * @return
   */
  private DatabaseReference initDb() {
    try {
      FileInputStream serviceAccount = new FileInputStream(
          serviceAccountFileName);
      FirebaseOptions options = new FirebaseOptions.Builder()
          .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
          .setDatabaseUrl(dbUrl).build();
      FirebaseApp.initializeApp(options);
    } catch (FileNotFoundException e) {
      new IllegalArgumentException("Illegal privacy key file");
    }
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    return db.getReference(queuePath + "/events");
  }
}
