# Firebase Queue java server


## Quick start

* Create Firebase service account 
    * `https://console.firebase.google.com` "project" > "settings" > "service account"
* Create file service-account.json and copy privacy key.
* Set firebase url `Queue.FIREBASE_URL = "https://{db_name}.firebaseio.com"`

### Run as standalone server
```
gradle run
```

### Use and library
```
import com.sm.firebasequeue.Queue
.
.
.

Queue
    .db(FIREBASE_URL, FIREBASE_KEY_FILE_NAME)
    .queuePath("queue")
    .messageHandler(new MessageListener() {
       @Override
        public void handle(Message message) {
        }
      })
    .start();
```


### References
* https://firebase.google.com/docs/admin/setup
* https://github.com/firebase/quickstart-java
