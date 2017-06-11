package com.github.thunder413.netrequest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;
/**
 * NetRequest
 *  <p>
 *      Build on top of HttpRequest lib that use NetConnection
 *      This class allow basic network operation such as performing a GET or POST request
 *          @see RequestMethod#GET,RequestMethod#POST
 *      You can keep track of the ongoing operation by attaching a listener
 *          @see OnNetResponse
 *          witch has two callback
 *          @see OnNetResponse#onNetResponseError(NetError),
 *          @see OnNetResponse#onNetResponseCompleted(NetResponse)
 *          the first one is fired when an error occures before or after network operation task beeing performed
 *          and the last when server response has been retrieved and the response was successfully parsed to Json
 *  </p>
 *
 * @author thunder413
 * @version 1.3
 */
@SuppressWarnings("WeakerAccess")
public class NetRequest {
    /**
     * LOG TAG
     */
    private static final String LOG_TAG = "NetRequest";
    /**
     * NetRequest context
     */
    private final Context context;
    /**
     * Application context
     */
    private final Context applicationContext;
    /**
     * Current method
     * default #METHOD_GET
     */
    private RequestMethod method = RequestMethod.GET;
    /**
     * Current requestDataType
     * @see RequestDataType
     */
    private RequestDataType requestDataType = RequestDataType.TEXT;
    /**
     * NetRequest uri
     */
    private String uri;
    /**
     * NetRequest data
     * @see NetParameter
     */
    private ArrayList<NetParameter> parameters = new ArrayList<>();
    /**
     * NetResponse listener
     * @see OnNetResponse
     */
    private OnNetResponse listener;
    /**
     * Tell whether or not an error occurred while building request
     */
    private boolean hasError;
    /**
     * NetRequest tag
     */
    private Object tag;
    /**
     * Whether or not to cancel app when context is diying
     */
    private boolean cancelOnContextDie;
    /**
     * Constructor
     * @param context Context
     */
    public NetRequest(Context context){
        this.context = context;
        applicationContext = context.getApplicationContext();
        // SetDefault RequestDataType
        setRequestDataType(RequestDataType.TEXT);
        // SetDefaultMethod
        setRequestMethod(RequestMethod.GET);
        // SetCancel on die context
        setCancelOnContextDie(true);
        // Generate default tag
        tag = String.valueOf(System.currentTimeMillis());
    }
    /**
     * Get active network
     * @return NetworkInfo Object
     */
    private boolean isNetworkActive() {
        if(applicationContext != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null;
        } else {
            return false;
        }
    }
    /**
     * Print a debug message
     * @param message Message to print
     */
    private void debug(String message){
        if(!NetRequestManager.getInstance().isDebug()){
            return;
        }
        Log.d(LOG_TAG,message);
    }
    /**
     * Print an error message
     * @param message Message to print
     */
    private void error(String message){
        // Check if debug is enabled in the manager
        if(!NetRequestManager.getInstance().isDebug()){
            return;
        }
        Log.e(LOG_TAG,message);
    }
    /**
     * Dispatch an error to response listener
     * @param error Error type
     */
    private void dispatchError(NetErrorStatus error) {
        hasError = true;
        if(listener != null) {
            listener.onNetResponseError(new NetError(error, tag));
        }
    }
    /**
     * Get context
     * @return Context
     */
    public Context getContext(){
        return this.context;
    }

    /**
     * Set Tag
     * @param tag Object
     */
    public void setTag(Object tag) {
        this.tag = tag;
    }

    /**
     * Get request TAG
     * @return Current assigned TAG
     */
    public Object getTag() {
        return tag;
    }

    /**
     * Get request method
     * @return Method
     */
    public RequestMethod getMethod() { return this.method; }

    /**
     * Set request method
     * @param method Method
     */
    public void setRequestMethod(RequestMethod method){
        this.method = (method == null) ? RequestMethod.GET:method;
    }

    /**
     * Set RequestDataType
     * @param requestDataType RequestDataType
     */
    public void setRequestDataType(RequestDataType requestDataType){
        this.requestDataType = requestDataType;
    }

    /**
     * Get requestDataType
     * @return Current RequestDataType
     */
    public RequestDataType getRequestDataType(){
        return requestDataType;
    }

    /**
     * Set uri
     * @param uri Uri string
     */
    public void setRequestUri(String uri) {
        this.uri = uri;
    }

    /**
     * Get request uri
     * @return Uri
     */
    public String getRequestUri(){
        return uri;
    }
    /**
     * Load uri
     * @param uri Url string
     */
    public void load(String uri)  {
        setRequestUri(uri);
        load();
    }
    /**
     * Load the default uri
     */
    public void load() {
        if(hasError) return;
        if (uri == null || uri.isEmpty()) {
            dispatchError(NetErrorStatus.INVALID_URI_ERROR);
            return;
        }
        if(!isNetworkActive()) {
            dispatchError(NetErrorStatus.CONNECTION_ERROR);
            return;
        }
        debug("Loading uri >> "+uri);
        NetRequestManager.getInstance().addToQueue(this);
    }
    /**
     * Set
     * @return Whether or not to cancel when context is dieing
     */
    public boolean isCancelOnContextDie(){
        return cancelOnContextDie;
    }

    /**
     * Get cancel on context die state
     * @param cancelOnContextDie State
     */
    public void setCancelOnContextDie(boolean cancelOnContextDie){
        this.cancelOnContextDie = cancelOnContextDie;
    }
    /**
     * Add a request parameter
     * @param name Name
     * @param value value
     */
    public void addParameter(String name, Object value){
        parameters.add(new NetParameter(name,value));
    }
    /**
     * Add a hash map as parameters
     * @param map DataMap
     */
    public void addParameterSet(Map<String,Object> map){
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            addParameter(entry.getKey(), entry.getValue());
        }
    }
    /**
     * Get parameters
     * @return String serialized
     */
    public String getParameters(){
        String params = "";
        for(int i = 0; i < parameters.size(); i++){
            params += parameters.get(i).toString();
            if(i+1 < parameters.size()) {
                params+="&";
            }
        }
        return params;
    }
    /**
     * Get response listener
     * @return OnNetResponseListener
     */
    public OnNetResponse getResponseListener(){
        return listener;
    }

    /**
     * Set response listener
     * @param listener OnNetResponse
     */
    public void setOnResponseListener(OnNetResponse listener){
        this.listener = listener;
    }

    /**
     * By default whenever the context die the request will be cancelled
     * @see NetRequest#setCancelOnContextDie(boolean)
     * if the request is already runing your onResponseListener wont be triggered
     * but you can also cancel the request manualy using this method
     */
    public void cancel(){
        NetRequestManager.getInstance().cancel(this);
    }
}