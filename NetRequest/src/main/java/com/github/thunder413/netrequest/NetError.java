package com.github.thunder413.netrequest;

/**
 * NetError
 * <p>
 *     Define all errors type thrown by
 *     @see NetRequest,NetRequestManager,NetRequestTask
 * </p>
 * @version 2.3
 * @author Cheikh Semeta
 */
@SuppressWarnings("all")
public class NetError {
    /**
     * Thrown when not network connectivity is detected
     */
    public static final String CONNECTION_ERROR = "connection_error";
    /**
     * Thrown when no uri is submitted
     */
    public static final String EMPTY_URI_ERROR = "empty_uri_error";
    /**
     * Thrown when attempt to set a null on request uri
     */
    public static final String NULL_URI_ERROR = "null_uri_error";
    /**
     * Thrown by any exception thrown by
     * @see android.net.Uri#parse(String)
     */
    public static final String INVALID_URI_ERROR = "invalid_uri_error";
    /**
     * Thrown when HttpRequest fail for some reason
     */
    public static final String  SERVER_ERROR = "server_error";
    /**
     * Thrown when fail to parse server response to Json
     */
    public static final String  PARSE_ERROR  = "parse_error";
    /**
     * Error type
     */
    private final String error;
    /**
     * Request tag
     */
    private final Object tag;
    /**
     * Constructor
     * @param error
     * @param tag Error to set
     */
    public NetError(String error, Object tag) {
        this.error = error;
        this.tag = tag;
    }

    /**
     * Get registered error
     * @return Error type
     */
    public String getError(){
        return error;
    }

    @Override
    public String toString() {
        return error;
    }

    /**
     * Get Request tag
     * @return
     */
    public Object getTag() {
        return tag;
    }
}
