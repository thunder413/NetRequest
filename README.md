# NetRequest [![Build Status](passing.svg)](passing.svg)

This lightweight library makes it easy to use HTTP requests. Although  the purpose is to make the use of Json data easy, it also allows to retrieve the server response using other formats, XML and TEXT.

This library is available under the [MIT License](http://www.opensource.org/licenses/mit-license.php).

## Usage

The NetRequet library is available from [JitPack](https://jitpack.io/#thunder413/NetRequest/1.2).

First add JitPack dependency line in your project `build.gradle` file:

```xml
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

And then simply add the following line to the `dependencies` section of your app module `build.gradle` file:

```groovy
compile 'com.github.thunder413:NetRequest:1.3'
```

Javadocs are available [here](http://https://github.com/thunder413/NetRequest/apidocs/index.html).

## FAQ

### What is the Purpose?

I'm sure that every android developer notice that most of the time when it come to make HTTP request it's about pulling structured data from the server such as `JSON` or `XML` , of course there are other libraries that handle quiet well the same kind of data but they are also shipped with other bunch of parsers that you may not need. In the other hand it's a matter of taste regarding the callbacks and how the request is built which drives me to develop this library

The Library is built on top of [kevinsawicki http-request library](https://github.com/kevinsawicki/http-request) which is a great library for using an [HttpURLConnection](http://download.oracle.com/javase/6/docs/api/java/net/HttpURLConnection.html) and by the way if you are interested in handling Http request by yourself, I highly recommend you to give it a try

### How are requests managed?

The `NetRequest` library is queue
based which mean that requests are stored in a pool and then executed, but you can control the execution mode while in the most case you will need the requests to be run in parallel (which is the default behavior) you can also turn that off and make the pool executor to run requests in sequence using `setParallelRequestEnabled(boolean state)`.


## How To ?

### Initialize manager

NetRequest has a manager but it auto-manage itself and using it is optional however it is useful in some cases, especially when you have repeating treatment, like always have to send a ``user_id`` every time you perform an http request. It also allow you to control the library behavior like printing debug in development mode or enabling/disabling ``parallel execution`` .

I recommend you to do it once in ``onCreate``  of your application class but you can use it anywhere you would like.

```java
NetRequestManager.getInstance()
  .setDebug(true) // Enable debug false default
  .setParallelRequestEnabled(true) // Default
  .addParamter("user_id",1) // send user_id each time a request is made
  .addParameter("username","john") // same as user_id
  .addParamters(Map<String,Object> map) // or add parameters as map
  ;
```

### Build and execute a request

```java
NetRequest netRequest = new NetRequest(context);
netRequest.load("http://google.com");
```

You can also use ``setRequestUri`` to set your url

```java
NetRequest netRequest = new NetRequest(context);
netRequest.setRequestUri("http://google.com");
netRequest.load();
```

### Perform a GET/POST Request

The default method is  ``GET``

```java
netRequest.setRequestMethod(RequestMethod.GET|RequestMethod.POST);
netRequest.load("http://google.com");
```

### Adding query parameters

```java
netRequest.addParameter("id",1);
netRequest.addParameter("username","john");
netRequest.addParameter("password","********");
netRequest.load("http://google.com");
```

### Using map as query parameters

```java
// Build your map
Map<String,Object> map = new HashMap<>();
        map.put("id",1);
        map.put("username","john");
        map.put("password","*****");
netRequest.addParameterSet(map);
netRequest.load("http://google.com");
```

### What kind of data are you expecting ?

The default expected data is ``RequestDataType.JSON`` but you can also set it up using ``setRequestDataType``  

```java
netRequest.setRequestDataType(RequestDataType.XML);
netRequest.load("http://google.com/xml");
```

### Get the request ressult

```java
netRequest.setOnResponseListener(new OnNetResponse() {
  @Override
  public void onNetResponseCompleted(NetResponse response) {
    Log.d("TAG",response.toString());
  }
  
  @Override
  public void onNetResponseError(NetError error) {
    Log.d("TAG",error.toString());
  }
});
netRequest.load("http://google.com");
```

``setOnResponseListener`` expect an interface so you can make your activity or fragment implement ``OnNetResponseListener`` class

### Handle response

```java
...
  @Override
  public void onNetResponseCompleted(NetResponse response) {
  	// Get response
    Log.d("TAG",response.toString());
    // If you are expecting json data just use
    JsonObject data = response.toJson();
    // If you are exepecting xml data just use
    Document xml = response.toXML();
    // You can also rely on the dataType from response
    RequestDataType dataType = response.getRequestDataType();
    if(dataType.equals(RequestDataType.JSON)) {
      
    } else if(dataType.equals(RequestDataType.XML)){
      
    }
  }
...
```

### Handle errors

```java
...
  @Override
  public void onNetResponseError(NetError error) {
  	Log.d("TAG",error.toString());
    // Handle error
    switch (error.getStatus()) {
    	case CONNECTION_ERROR: // No internet connection detected
        break;
        case PARSE_ERROR: // Fail to parse data into request data type
       	break;
        case ERROR: // Error
        break;
        case INVALID_URI_ERROR: // when no uri is supplied or null
        break;
        case NOT_FOUND: // HttpStatus 404
        break;
        case BAD_GATEWAY: // HttpStatus 502
        break;
        case SERVER_ERROR: // Any other HTTPStatus error 
        break;  
      	case REQUEST_ERROR: // Internal module error
        break;
        case CANCELED: // Request cancelled
        break;
 	}
  }
...
```

### Cancel a request

You can cancel a request at any time using ``cancel`` on your netRequest ``instance`` 

```java
// Just call cancel on your netRequest instance
NetRequet netRequest = new NetRequest(context);
netRequest.load("http://google.com");
// Later
netRequest.cancel()
```

### Request TAG

``NetRequest`` allow you to use tags, this is usefull when in one activity or fragment you have to make multiple request you can re-use the ``OnResponseListener`` 

```java
...
// Tag can be any data type you would like (Integer,String,Object ....)
netRequest.setTag(tag);
netRequest.setOnResponseListener(new OnNetResponse() {  
  @Override  
  public void onNetResponseCompleted(NetResponse response) {
    Log.d("TAG",response.toString()); 
    // Deal with tag
    Object tag = response.getTag();
  }   
  @Override  
  public void onNetResponseError(NetError error) {    
    Log.d("TAG",error.toString());
    // Deal with tag
    Object tag = error.getTag();
  }
});
netRequest.load("http://google.com");
```



## Author

- **Thunder413** (https://github.com/thunder413)

## License

This project is licensed under the  [MIT License](http://www.opensource.org/licenses/mit-license.php) 

## Acknowledgments

- kevinsawicki [http-request](https://github.com/kevinsawicki/http-request) 
- Google [Gson](https://github.com/google/gson) 