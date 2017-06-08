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
 *          @see NetRequest#METHOD_GET,NetRequest#METHOD_POST
 *      You can keep track of the ongoing operation by attaching a listener
 *          @see OnNetResponse
 *          witch has two callback
 *          @see OnNetResponse#onNetResponseError(NetError),OnNetResponse#onNetResponseCompleted(NetResponse)
 *          the first one is fired when an error occures before or after network operation task beeing performed
 *          and the last when server response has been retrieved and the response was successfully parsed to Json
 *  </p>
 * @version 1.2
 * @author Thunder413
 */
@SuppressWarnings("all")
public class NetRequest {
    /**
     * LOG TAG
     */
    private static final String LOG_TAG = "NetRequest";
    /**
     * Method GET
     */
    public static final String METHOD_GET = "GET";
    /**
     * Method POST
     */
    public static final String METHOD_POST = "POST";
    /**
     * NetRequest context
     */
    private final Context context;
    /**
     * Current method
     * @default #METHOD_GET
     */
    private String method = METHOD_GET;
    /**
     * NetRequest uri
     */
    private Uri uri;
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
    private boolean canceOnContextDie;
    /**
     * Constructor
     * @param context Context
     */
    public NetRequest(Context context){
        this.context = context.getApplicationContext();
        tag = String.valueOf(System.currentTimeMillis());
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
     * Get context
     * @return Context
     */
    public Context getContext(){
        return this.context;
    }
    /**
     *Dispatch an error to response lsietner
     * @param error Error type
     */
    private void dispatchError(String error) {
        hasError = true;
        if(listener != null) {
            listener.onNetResponseError(new NetError(NetError.SERVER_ERROR, error));
        }
    }
    /**
     * Get / Set request method
     * @return Method
     */
    public String getMethod() { return this.method; }
    public void setRequestMethod(String method){
        this.method = (method == null || method.isEmpty()) ? METHOD_GET:method;
    }
    /**
     * Get / Set uri
     * @return Uri
     */
    public Uri getRequestUri(){ return this.uri; }
    public void setRequestUri(Uri uri)  {
        this.uri = uri;
    }
    /**
     * Set uri
     * @param uri Uri string
     */
    public void setRequestUri(String uri) {
        if(uri == null) {
            dispatchError(NetError.NULL_URI_ERROR);
            return;
        } else if(uri.isEmpty()){
            dispatchError(NetError.EMPTY_URI_ERROR);
            return;
        }
        final Uri parsedUri = Uri.parse(uri);
        setRequestUri(parsedUri);
        /*
        if(URLUtil.isNetworkUrl(uri)) {

        } else {
            error("SetRequest :: Submitted uri is not valid >> "+uri);
            dispatchError(NetError.INVALID_URI_ERROR);
        }*/
    }

    /**
     * Get / Set
     * @return Whether or not to cancel when context is diying
     */
    public boolean isCanceOnContextDie(){
        return canceOnContextDie;
    }
    public void setCancelOnContextDie(boolean cancelOnContextDie){
        this.canceOnContextDie = cancelOnContextDie;
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
     * Get / Set response listener
     * @return Current binded listener
     */
    public OnNetResponse getResponseListener(){
        return listener;
    }
    public void setOnResponseListener(OnNetResponse listener){
        this.listener = listener;
    }
    /**
     * Get active network
     * @return NetworkInfo Object
     */
    private boolean isNetworkActive() {
        if(context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null;
        } else {
            return false;
        }
    }
    /**
     * Load Uri
     * @param uri Uri object
     */
    public void load(Uri uri)  {
        setRequestUri(uri);
        load();
    }
    /**
     * Load uri
     * @param uri Url string
     */
    public void load(String uri)  {
        if(hasError) return;
        if(uri == null) {
            dispatchError(NetError.NULL_URI_ERROR);
            return;
        } else if(uri.isEmpty()){
            dispatchError(NetError.EMPTY_URI_ERROR);
            return;
        }
        setRequestUri(uri);
        load(this.uri);
    }
    /**
     * Load the default uri
     */
    public void load() {
        if(hasError) return;
        if (uri == null) {
            dispatchError(NetError.NULL_URI_ERROR);
            return;
        }
        if(!isNetworkActive()) {
            dispatchError(NetError.CONNECTION_ERROR);
            return;
        }
        debug("Loading uri >> "+uri);
        NetRequestManager.getInstance().addToQueue(this);
    }
    /**
     * Cancel
     */
    public void cancel(){
        NetRequestManager.getInstance().cancel(this);
    }

}