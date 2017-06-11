package com.github.thunder413.netrequest;

/**
 * NetErrorStatus
 *
 * <p>Different possible status whether while building request or while or after request execution</p>
 *
 * @author thunder413
 * @version 1.3
 */
@SuppressWarnings("WeakerAccess")
public enum NetErrorStatus {
    /**
     * An Http error
     */
    ERROR,
    /**
     * Parse error occurs while parsing data according to given RequestDataType
     * @see RequestDataType
     */
    PARSE_ERROR,
    /**
     * Internal request error
     */
    REQUEST_ERROR,
    /**
     * Url not found
     */
    NOT_FOUND,
    /**
     * Bad gateway
     */
    BAD_GATEWAY,
    /**
     * Network connection error, occurs when no internet connection has been detected
     */
    CONNECTION_ERROR,
    /**
     * Invalid uri
     */
    INVALID_URI_ERROR,
    /**
     * Server response error
     */
    SERVER_ERROR,
    /**
     * Request task canceled
     */
    CANCELED,
    /**
     * Request task success
     */
    SUCCESS
}
