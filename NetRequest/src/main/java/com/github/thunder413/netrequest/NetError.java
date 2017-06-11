package com.github.thunder413.netrequest;

/**
 * NetError
 * <p>
 *     Define all errors type thrown by
 *     @see NetRequest,NetRequestManager,NetRequestTask
 * </p>
 * @version 1.3
 * @author Thunder413
 */
@SuppressWarnings("WeakerAccess")
public class NetError {
    /**
     * Error type
     */
    private final NetErrorStatus status;
    /**
     * Request tag
     */
    private final Object tag;
    /**
     * Constructor
     * @param status ErrorStatus
     * @param tag Error to set
     */
    public NetError(NetErrorStatus status, Object tag) {
        this.status = status;
        this.tag = tag;
    }

    /**
     * Get registered status
     * @return Error type
     */
    public NetErrorStatus getStatus(){
        return status;
    }
    /**
     * Get Request tag
     * @return Object
     */
    public Object getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return String.valueOf(status);
    }


}
