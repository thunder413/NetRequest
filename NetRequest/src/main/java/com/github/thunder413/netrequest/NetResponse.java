package com.github.thunder413.netrequest;

import com.google.gson.JsonObject;

import org.w3c.dom.Document;

/**
 * NetResponse
 * <p>
 *     A wrapper for server response but available only when
 *     server response has been parsed successfully
 * </p>
 * @version 1.2
 * @author Thunder413
 */
@SuppressWarnings("all")
public class NetResponse {
    /**
     * ResponseText
     */
    private final String responseText;
    /**
     * Rresponse Json
     */
    private JsonObject responseJson;
    /**
     * Response XML
     */
    private Document responseXML;
    /**
     * RequestDataType
     */
    private final RequestDataType requestDataType;

    /**
     * ResponseTag
     */
    private Object tag;

    /**
     * Constructor
     * @param body Http Response
     * @param tag NetRequest tag
     * @param requestDataType NetRequest data type
     * @param data Data
     */
    public NetResponse(String body, Object tag, RequestDataType requestDataType, Object data){
        this.responseText = body;
        this.requestDataType = requestDataType;
        if(requestDataType.equals(RequestDataType.JSON)){
            responseJson = (JsonObject)data;
        } else if(requestDataType.equals(RequestDataType.XML)){
            responseXML = (Document)data;
        }
    }
    @Override
    public String toString() {
        return responseText;
    }
    /**
     * Get response as JsonObject
     * @return JsonObject
     */
    public JsonObject toJson() {
       return responseJson;
    }

    /**
     * Get response as XML Document
     * @return Document XML
     */
    public Document toXML(){
        return responseXML;
    }
    /**
     * Get reesponse tag
     * @return Tag
     */
    public Object getTag(){
        return  tag;
    }

    /**
     * Get request dataType
     * @return DataType
     */
    public RequestDataType getRequestDataType() {
        return requestDataType;
    }
}