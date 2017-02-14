package com.sm.sample;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Runner {
  private static final String DATABASE_URL =
      "https://DB.firebaseio.com";

  public static void main(String[] args)
      throws InterruptedException, FileNotFoundException {
    System.out.println(">>>> starting...");

    FileInputStream serviceAccount =
        new FileInputStream("service-account.json");
    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
        .setDatabaseUrl(DATABASE_URL).build();
    FirebaseApp.initializeApp(options);

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    database.child("catalog").addChildEventListener(new ChildEventListener() {

      @Override
      public void onChildRemoved(DataSnapshot snapshot) {
      }

      @Override
      public void onChildMoved(DataSnapshot snapshot, String prevChildName) {
      }

      @Override
      public void onChildChanged(DataSnapshot snapshot, String prevChildName) {
        System.out.println(">>>> onChange key: " + snapshot.getKey()
            + " value: " + snapshot.getValue(Item.class));

      }

      @Override
      public void onChildAdded(DataSnapshot snapshot, String prevChildName) {
        System.out.println(">>>> onAdded key: " + snapshot.getKey() + " value: "
            + snapshot.getValue(Item.class));

      }

      @Override
      public void onCancelled(DatabaseError error) {
      }
    });

    for (int i = 0; i < 10; i++) {
      Thread.sleep(5000);
      database.child("catalog").push()
          .setValue(new Item("sku_s" + i, "name_s" + i, 123.45 + i));
    }

    Thread.currentThread().join();
  }
}
