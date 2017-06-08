package com.github.thunder413.netrequest;

/**
 * OnNetResponse
 * <p>An interface build to handle NetRequest response events</p>
 * @version 1.2
 * @author Thunder413
 */
public interface OnNetResponse {
    /**
     * Fires when server response has been loaded and response body has
     * been successfully parsed to Json
     *      @see com.google.gson.Gson
     * @param response NetResponse
     *                 @see NetResponse
     */
    void onNetResponseCompleted(NetResponse response);
    /**
     * Fires when an error occurs while building net request or while performing network task
     *  called with an error type
     *      @see NetError#CONNECTION_ERROR,NetError#SERVER_ERROR,NetError#PARSE_ERROR ...
     * @param error NetError
     */
    void onNetResponseError(NetError error);
}