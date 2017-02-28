package com.sm.firebasequeue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Queue {
  private static final String DEFAULT_QUEUE_PATH = "queue";

  private String dbUrl;
  private String serviceAccountFileName;
  private String queuePath = DEFAULT_QUEUE_PATH;
  private MessageListener messageListener;

  public static void main(String[] args)
      throws FileNotFoundException, InterruptedException {
    System.out.println(">>> queue starting...");

    Queue
        .db("https://catalogsample-cafa7.firebaseio.com",
            "service-account.json")
        .queuePath("queue").messageHandler(new SimpleMessageHandler())
        .start(1);

    Thread.currentThread().join();
  }

  public static Queue db(String dbUrl, String serviceAccountFileName)
      throws FileNotFoundException {
    Queue queue = new Queue();
    queue.dbUrl = dbUrl;
    queue.serviceAccountFileName = serviceAccountFileName;
    return queue;
  }

  public Queue messageHandler(MessageListener messageListener) {
    this.messageListener = messageListener;
    return this;
  }

  public Queue queuePath(String queuePath) {
    this.queuePath = queuePath;
    return this;
  }

  public void start() throws FileNotFoundException {
    startListener(new QueueExecutor(queuePath));
  }

  public void start(int threadPoolSize) throws FileNotFoundException {
    startListener(new QueueExecutor(queuePath, threadPoolSize));
  }

  private void startListener(QueueExecutor queueExecutor)
      throws FileNotFoundException {
    DatabaseReference queueRef = initDb();

    Query newMessageQuery = queueRef.orderByChild("header/state").equalTo("new")
        .limitToFirst(1);

    System.out
        .println(">>> waiting for message with \"header/state = new\" in path: "
            + queuePath + "/events");

    newMessageQuery.addChildEventListener(new EventHandler((eventSnapshot) -> {
      EventWorker messageWorker = new EventWorker(eventSnapshot,
          messageListener);
      queueExecutor.execute(messageWorker);
    }));
  }

  private DatabaseReference initDb() throws FileNotFoundException {
    FileInputStream serviceAccount = new FileInputStream(
        serviceAccountFileName);
    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
        .setDatabaseUrl(dbUrl).build();
    FirebaseApp.initializeApp(options);

    FirebaseDatabase db = FirebaseDatabase.getInstance();

    return db.getReference(queuePath + "/events");
  }
}
