# NetRequest

NetRequest is a simple HTTPRequest manager that make easy to deal with Json responses

### Requirements

NetRequest can be included in any Android application.

NetRequest supports Android 2.3 (Gingerbread) and later.

### Using NetRequest in your application

First add JitPack dependency line in your project `build.gradle` file

```groovy
allprojects {

    repositories {
     
        maven { url 'https://jitpack.io' }
    }
}
```

And then simply add the following line to the `dependencies` section of your `build.gradle` file:

```groovy
compile 'com.github.thunder413:NetRequest:1.2'
```
### Performing Get / Post request

```java
NetRequest netRequest = new NetRequest(context);
// Set Request method #NetRequest.METHOD_POST | #NetRequest.METHOD_GET (Default)
netRequest.setRequestMethod(NetRequest.METHOD_POST);
// Bind Listener
netRequest.setOnResponseListener(new OnNetResponse() {
       @Override
       public void onNetResponseCompleted(NetResponse response) {
           // Get response as string
           Log.d("TAG",response.toString());
           // Get response as JsonObject
            JsonObject data = response.toJson();
         }

         @Override
         public void onNetResponseError(NetError error) {
             // Handle error
             switch (error.getError()) {
                    
             }
         }
 });
 // Set Request uri (Support both uri and url string)
 netRequest.setRequestUri("http://demo.com/json");
 // Trigger request
 netRequest.load();
 // You can also pass the uri directly to load method
 netRequest.load("http://demo.com/json");
```

### Handle Errors 

```java
@Override
public void onNetResponseError(NetError error) {
	// Handle error
    switch (error.getError()) {
    	case NetError.EMPTY_URI_ERROR:
        	// No uri is set via setRequestUriMethod or no uri spupplied with load
        break;
        case NetError.INVALID_URI_ERROR:
        	// Supplied uri is not a valid http url
        break;
      	case NetError.NULL_URI_ERROR:
        	// Null uri 
        break;
        case NetError.CONNECTION_ERROR:
        	// No internet connection detected
        break;
        case NetError.PARSE_ERROR:
       		// Fail to parse server response
        break;  
      	case NetError.SERVER_ERROR:
        	// Server error probably no response from the server
        break;
    }
}
```



## Author

* **Thunder413** (https://github.com/thunder413)



## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* HTTPRequest from kevinsawicki
* Gson Google


