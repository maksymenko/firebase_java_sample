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

### Usage
* Create Spring bean and annotatate with `@FirebaseQueueSubscriber` annotation.
* Add input message handler by anitation method with `FirebaseQueueOnMessage` annotation.

```
@FirebaseQueueSubscriber
public class FirebaseQueueMessageSubscriber {

  @FirebaseQueueOnMessage(queueName = "dto_queue")
  public ResponseDto hello(RequestDto request) {
    return new ResponseDto("Hello " + request.getName()); 
  }
}
```


### References
* https://firebase.google.com/docs/admin/setup
* https://github.com/firebase/quickstart-java
