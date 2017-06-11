package com.github.thunder413.netrequest;
/**
 * NetParameter
 *
 * <p>Request parameter pair/value holder</p>
 *
 * @author thunder413
 * @version 1.3
 */
@SuppressWarnings("WeakerAccess")
public class NetParameter {
    /**
     * Parameter name
     */
    private String name;
    /**
     * Parameter value
     */
    private Object value;
    /**
     * Constructor
     * @param name Param name
     * @param value Param value
     */
    public NetParameter(String name,Object value) {
        this.name = name;
        this.value = value;
    }
    /**
     * Get parameter name
     * @return Parameter name
     */
    public String getName() { return name; }

    /**
     * Set parameter name
     * @param name Name
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Get parameter value
     * @return Value
     */
    public Object getValue() { return value; }

    /**
     * Set parameter  value
     * @param value Value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getName()+"="+String.valueOf(getValue());
    }
}