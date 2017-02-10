# Firebase and java


## Quick start

* Create Firebase service account 
    * `https://console.firebase.google.com` "project" > "settings" > "service account"
* Create file service-account.json and copy privacy key.
* `Runner.java` - set database name
```
gradle run
```

### Dependencies
* dependencies
```
compile 'com.google.firebase:firebase-admin:4.1.1'
```


### References
* https://firebase.google.com/docs/admin/setup
* https://github.com/firebase/quickstart-java
