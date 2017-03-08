package com.sm.firebase.queue;

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
 * 
 * <pre>
 * Expected message format:
 *    messageID : {
 *      header : {
 *       state: new,
 *       key:   value,
 *       ...
 *      }
 *    payload {
 *       key: value,
 *       ...
 *    }
 * </pre>
 */
public class Queue {
  private final static String MESSAGE_BROCKER_NAME = "messageBrocker";
  private final static int THREAD_POOL_SIZE = 4;
  private DatabaseReference messageBrokerRef;
  private QueueExecutor queueExecutor;

  /**
   * Creates queue instance based on Firebase parameters.
   *
   * @param dbUrl Firebase URL.
   * @param serviceAccountFileName file contains private key to firebase
   *          project.
   */
  public Queue(String dbUrl, String serviceAccountFileName) {
    this.messageBrokerRef = initDb(dbUrl, serviceAccountFileName);
    this.queueExecutor = new QueueExecutor(MESSAGE_BROCKER_NAME,
        THREAD_POOL_SIZE);
  }

  /**
   * Listen node and pass value to handler.
   * 
   * @param queueName - node path
   * @param listener - handler for incoming message
   */
  public void listenQueue(String queueName, MessageListener listener) {
    DatabaseReference queueRef = messageBrokerRef.child(queueName);

    MessageClaimer messageClaimer = new MessageClaimer(queueExecutor, listener,
        queueRef);
    messageClaimer.start();
  }

  /**
   * Firebase database initializer.
   * 
   * @return
   */
  private DatabaseReference initDb(String dbUrl,
      String serviceAccountFileName) {
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

    return db.getReference(MESSAGE_BROCKER_NAME);
  }

}
