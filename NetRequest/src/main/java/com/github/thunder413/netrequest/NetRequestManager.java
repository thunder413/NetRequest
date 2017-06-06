package com.github.thunder413.netrequest;

import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;

import com.github.thunder413.netrequest.utils.HttpRequest;
import com.github.thunder413.netrequest.utils.NetUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * NetRequestManager
 * <p>Manager for net request </p>
 * @author Cheikh Semeta
 * @version 2.0
 */
@SuppressWarnings("all")
public class NetRequestManager {
    /**
     * Log tag
     */
    private final String LOG_TAG = "NetRequestManager";
    /**
     * Instance
     */
    private static NetRequestManager _instance;
    /**
     * Enable disable
     */
    private boolean debug;
    /**
     * Map used to handle list of queued NetRequest
     */
    private Map<String,NetRequest> queue = new HashMap<>();
    /**
     * Map used to handle list of running task
     */
    private Map<String,NetRequestTask> taskMap = new HashMap<>();
    /**
     * Map used to hold list of http request
     */
    private Map<String,HttpRequest> requestMap = new HashMap<>();
    /**
     * Default parameters bind while initialization
     */
    private ArrayList<NetParameter> defaultParameters = new ArrayList<>();
    /**
     * Private constructor
     */
    private NetRequestManager(){}
    /**
     * Print a debug message
     * @param message Message to print
     */
    private void debug(String message){
        if(!isDebug()){
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
        if(!isDebug()){
            return;
        }
        Log.e(LOG_TAG,message);
    }
    /**
     * Singleton instance
     * @return Instance
     */
    public static NetRequestManager getInstance() {
        if(_instance == null) {
            _instance = new NetRequestManager();
        }
        return  _instance;
    }
    /**
     * Enable / Disable
     * @return State True if enable false otherwise
     */
    public boolean isDebug() {
        return debug;
    }
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    /**
     * Tell whether or not a given neRequest has been add to queue
     * @param key A Hash created from request uri
     * @return State True if the already queued False otherwise
     */
    private boolean isQueued(String key) {
        return (queue.size() > 0 && queue.containsKey(key));
    }
    /**
     * Tell whether or not a given neRequest task is being performed
     * @param key A Hash created from request uri
     * @return State True if the already running False otherwise
     */
    private boolean isRunning(String key){
        return (taskMap.size() > 0 && taskMap.containsKey(key) && taskMap.get(key).getStatus()!=null
        && taskMap.get(key).getStatus() == AsyncTask.Status.RUNNING);
    }
    /**
     * Build a hah for a given request
     * @param netRequest NetRequest
     * @return Hash key
     */
    private String getKey(NetRequest netRequest){
        if(netRequest.getRequestUri() != null) {
            String uri = netRequest.getRequestUri().toString();
            uri = appendParameters(uri, getParameters());
            uri = appendParameters(uri, netRequest.getParameters());
            return NetUtils.md5(uri);
        }else {
            return NetUtils.md5("default_key");
        }
    }
    /**
     * Append parameters to an uri
     * @param url Url to modifier
     * @param params Parameters
     * @return Updated url
     */
    private String appendParameters(String url,String params){
        if(params == null  || params.isEmpty()) {
            return url;
        }
        if(url.contains("?")) {
            url+= "&"+params;
        } else {
            url +="?"+params;
        }
        return url;
    }
    /**
     * Take #NetRequest as a parameter and build an http request
     *          @see HttpRequest
     * @param netRequest NetRequest
     * @return HttpRequest
     */
    private HttpRequest buildRequest(NetRequest netRequest) {
        HttpRequest request;
        // Create url string with default parameters
        String url = appendParameters(netRequest.getRequestUri().toString(),
                getParameters());

        if(netRequest.getMethod().equals(NetRequest.METHOD_POST)) {
            request = HttpRequest.post(url);
        } else {
            // Add parameters
            url = appendParameters(url,netRequest.getParameters());
            request = HttpRequest.get(url);
        }
        debug("BuildRequest >> Url >> "+url);
        request.acceptGzipEncoding().uncompress(true);
        return request;
    }
    /**
     * Add a request parameter
     * @param name Name
     * @param value value
     */
    public void addParameter(String name, Object value){
        defaultParameters.add(new NetParameter(name,value));
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
        for(int i = 0; i < defaultParameters.size(); i++){
            params += defaultParameters.get(i).toString();
            if(i+1 < defaultParameters.size()) {
                params+="&";
            }
        }
        return params;
    }
    /**
     * Add a net request to a queue
     * @param netRequest Net request
     */
    public void addToQueue(NetRequest netRequest) {
        String key = getKey(netRequest);
        if(!isQueued(key)) {
            queue.put(key,netRequest);
            if(!isRunning(key)) {
                requestMap.put(key, buildRequest(netRequest));
                taskMap.put(key, new NetRequestTask(netRequest));
                AsyncTaskCompat.executeParallel(taskMap.get(key), requestMap.get(key));
            } else {
                debug("AddToQueue >> Already running >> ("+netRequest.getRequestUri()+")");
            }
        } else {
            debug("AddToQueue >> Already queued and probably running shortly ");
        }
    }
    /**
     * Cancel a netRequest
     * @param netRequest NetRequest
     */
    public void cancel(NetRequest netRequest){
        String key = getKey(netRequest);
        if (isRunning(key)){
            NetRequestTask task = taskMap.get(key);
            task.cancel(true);
        }
        removeFromQueue(netRequest);
    }
    /**
     * Remove a request from queue
     * @param netRequest NetRequest
     */
    public void removeFromQueue(NetRequest netRequest) {
        String key = getKey(netRequest);
        if(queue.containsKey(key))
            queue.remove(key);
        if(requestMap.containsKey(key))
            requestMap.remove(key);
        if(taskMap.containsKey(key))
            taskMap.remove(key);
    }
}