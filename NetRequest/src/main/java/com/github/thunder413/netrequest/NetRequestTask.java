package com.github.thunder413.netrequest;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.thunder413.netrequest.utils.HttpRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * NetRequestTask
 *
 * <p>Executes the HTTP request passed as argument and sends the result to the listener
 * according to result status if RequestStatus#SUCCESS it will trigger the
 * {OnNetResponse#onResponseCompleted} method  otherwise {OnNetResponse#onResponseError}
 * </p>
 *
 * @author thunder413
 * @version 1.3
 */
@SuppressWarnings("WeakerAccess")
public class NetRequestTask extends AsyncTask<HttpRequest,Void,NetErrorStatus> {
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
     * Server response XML
     */
    private Document responseXML;
    /**
     * Task status
     */
    private NetErrorStatus status = NetErrorStatus.ERROR;
    /**
     * NetRequest
     */
    private final NetRequest netRequest;


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
        status = NetErrorStatus.CANCELED;
    }

    /**
     * Parse content to json
     * @param content Content
     * @return JsonObject
     */
    private JsonObject getAsJson(String content){
        try {
            return new JsonParser().parse(content).getAsJsonObject();
        } catch (JsonParseException e){
            if (NetRequestManager.getInstance().isDebug()) {
                // Debug
                error("DoInBackground >> Server response parse error");
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * Parse content to XML Document
     * @param content Content
     * @return Document
     */
    private Document getAsXML(String content){
        Document doc;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse( new InputSource( new StringReader( content ) ) );
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
    /**
     * Perform a computation on a background thread.
     *
     * @param params HttpRequest.
     * @return Status.
     */
    @Override
    protected NetErrorStatus doInBackground(HttpRequest... params) {
        request = params[0];
        if(!canGoFurther()) {
            // Cancel
            debug("DoInBackground >> Can go further due to task cancel");
            NetRequestManager.getInstance().removeFromQueue(netRequest);
            return NetErrorStatus.CANCELED;
        }
        try {
            debug("DoInBackground >> Performing request on url");
            if(netRequest.getMethod() == RequestMethod.POST){
                request.send(netRequest.getParameters());
            }
            responseText = request.body();
            if(request.ok()) {
                // Debug
                debug("DoInBackground >> Response >> " + responseText + " ResponseStatus >> " + request.code());
                RequestDataType requestDataType = netRequest.getRequestDataType();
                if (requestDataType.equals(RequestDataType.JSON)) {
                    responseJson = getAsJson(responseText);
                    if (responseJson == null) {
                        status = NetErrorStatus.PARSE_ERROR;
                    } else {
                        status = NetErrorStatus.SUCCESS;
                    }
                } else if (requestDataType.equals(RequestDataType.XML)) {
                    responseXML = getAsXML(responseText);
                    if (responseXML == null) {
                        status = NetErrorStatus.PARSE_ERROR;
                    } else {
                        status = NetErrorStatus.SUCCESS;
                    }
                } else {
                    status = NetErrorStatus.SUCCESS;
                }
            } else if(request.code() == 404) {
                status = NetErrorStatus.NOT_FOUND;
            } else if(request.code() == 502) {
                status = NetErrorStatus.BAD_GATEWAY;
            }  else  {
                status = NetErrorStatus.SERVER_ERROR;
            }
        } catch (Exception e){
            if (NetRequestManager.getInstance().isDebug()) {
                // Debug
                error("DoInBackground >> HttpRequest error");
                e.printStackTrace();
            }
            status = NetErrorStatus.REQUEST_ERROR;
        }
        return status;
    }

    @Override
    protected void onPostExecute(NetErrorStatus status) {
        super.onPostExecute(status);
        debug("OnPostExecute >> Status : "+status);
        this.status = status;
        if(!canGoFurther()) {
            // Cancel
            debug("OnPostExecute >> Can go further due to task cancel");
            NetRequestManager.getInstance().removeFromQueue(netRequest);
            return;
        }
        // Get listener
        OnNetResponse listener          = netRequest.getResponseListener();
        RequestDataType requestDataType = netRequest.getRequestDataType();
        Object tag                      = netRequest.getTag();
        if (status == NetErrorStatus.SUCCESS){
            if(listener != null) {
                // Debug
                debug("OnPostExecute >> Status : Success >> Triggering listener");
                Object data = null;
                if(requestDataType.equals(RequestDataType.JSON)) {
                    data = responseJson;
                } else if(requestDataType.equals(RequestDataType.XML)){
                    data = responseXML;
                }
                NetResponse netResponse = new NetResponse(responseText,tag, requestDataType,data);
                listener.onNetResponseCompleted(netResponse);
            }
        } else {
            if(listener != null) {
                // Debug
                debug("OnPostExecute >> Status : Error >> Triggering listener");
                listener.onNetResponseError(new NetError(status,tag));
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
            if(netRequest.isCancelOnContextDie()) {
                if (context == null) {
                    go = false;
                } else {
                    if (context instanceof AppCompatActivity) {
                        AppCompatActivity activity = ((AppCompatActivity) context);
                        go = !activity.isFinishing();
                    }
                }
            }
        }
        return go;
    }
}
