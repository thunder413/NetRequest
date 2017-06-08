package com.github.thunder413.netrequest;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.thunder413.netrequest.utils.HttpRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * NetRequestTask
 * <p>HttpRequest performer </p>
 * @author Thunder413
 * @version 1.2
 */
@SuppressWarnings("all")
public class NetRequestTask extends AsyncTask<HttpRequest,Void,NetRequestTask.RequestStatus> {
    /**
     * Log tag
     */
    private static final String LOG_TAG = "NetRequestTask";
    /**
     * Current http request
     */
    private HttpRequest request;
    /**
     * Force cancel
     */
    private boolean canceled;
    /**
     * Server response text
     */
    private String responseText;
    /**
     * Server response Json
     */
    private JsonObject responseJson;
    /**
     * Task status
     */
    private RequestStatus status = RequestStatus.CANCELLED;
    /**
     * NetRequest
     */
    private final NetRequest netRequest;
    /**
     * Status
     */
    public enum RequestStatus {
        SUCCESS,
        ERROR,
        CANCELLED
    }

    /**
     * Constructor
     * @param netRequest NetRequest
     */
    public NetRequestTask(NetRequest netRequest) {
        this.netRequest  = netRequest;
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
        if(!NetRequestManager.getInstance().isDebug()){
            return;
        }
        Log.e(LOG_TAG,message);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        debug("OnPreExecute");
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        debug("OnCancelled");
        canceled = true;
        try {
            if(request != null) {
                request.disconnect();
            }
            error("Cancel >> HttpRequest >> disconnected");
        } catch (Exception e){
            if(NetRequestManager.getInstance().isDebug()) {
                error("Cancel >> HttpRequest >> failing disconnected");
                e.printStackTrace();
            }
        }
        status = RequestStatus.CANCELLED;
    }


    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected RequestStatus doInBackground(HttpRequest... params) {
        request = params[0];
        if(!canGoFurther()) {
            // Cancel
            debug("Can go further due to task cancel");
            NetRequestManager.getInstance().removeFromQueue(netRequest);
            return RequestStatus.CANCELLED;
        }
        try {
            debug("Performing request on url");
            if(netRequest.getMethod() == NetRequest.METHOD_POST){
                request.send(netRequest.getParameters());
            }
            responseText = request.body();
            // Debug
            debug("ServerResponse >> "+responseText);
            try {
                responseJson = new JsonParser().parse(responseText).getAsJsonObject();
                status = RequestStatus.SUCCESS;
            } catch (JsonParseException e){
                if (NetRequestManager.getInstance().isDebug()) {
                    // Debug
                    error("DoInBackground >> Server response parse error");
                    e.printStackTrace();
                }
                status = RequestStatus.ERROR;
            }
        } catch (Exception e){
            if (NetRequestManager.getInstance().isDebug()) {
                // Debug
                error("DoInBackground >> HttpRequest error");
                e.printStackTrace();
            }
            status = RequestStatus.ERROR;
        }
        return status;
    }

    @Override
    protected void onPostExecute(RequestStatus status) {
        super.onPostExecute(status);
        if(!canGoFurther()) {
            // Cancel
            debug("Can go further due to task cancel");
            NetRequestManager.getInstance().removeFromQueue(netRequest);
            return;
        }
        // Get listener
        OnNetResponse listener = netRequest.getResponseListener();

        if (status == RequestStatus.SUCCESS){
            if(listener != null) {
                // Debug
                debug("OnPostExecute : Status >> Success >> Triggering listener");
                listener.onNetResponseCompleted(new NetResponse(responseText,responseJson,netRequest.getTag()));
            }
        } else {
            if(listener != null) {
                // Debug
                debug("OnPostExecute : Status >> Error >> Triggering listener");
                listener.onNetResponseError(new NetError(NetError.SERVER_ERROR,netRequest.getTag()));
            }
        }
        NetRequestManager.getInstance().removeFromQueue(netRequest);
    }
    /**
     * Tell whether or not we should continue performing this request
     * @return True|False
     */
    private boolean canGoFurther(){
        boolean go = true;
        if(canceled) {
            return false;
        }
        Context context = netRequest.getContext();
        if(isCancelled()) {
            go = false;
        } else {
            if(netRequest.isCanceOnContextDie()) {
                if (context == null) {
                    go = false;
                } else {
                    if (context instanceof AppCompatActivity) {
                        AppCompatActivity activity = ((AppCompatActivity) context);
                    }
                }
            }
        }
        return go;
    }
}
