package com.github.thunder413.netrequest;

import com.google.gson.JsonObject;

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
    private final JsonObject responseJson;
    /**
     * ResponseTag
     */
    private Object tag;
    /**
     * Constructor
     * @param body JsonString
     * @param bodyJson JsonObject
     */
    public NetResponse(String body, JsonObject bodyJson, Object tag){
        this.responseText = body;
        this.responseJson = bodyJson;
        this.tag = tag;
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
     * Get reesponse tag
     * @return Tag
     */
    public Object getTag(){
        return  tag;
    }
}