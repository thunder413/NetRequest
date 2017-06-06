package com.github.thunder413.netrequest;
/**
 * NetParameter
 *  <p>
 *      Request parameter pair/value holder
 *  </p>
 *  @version 2.3
 *  @author Cheikh Semeta
 */
@SuppressWarnings("all")
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
     * Get / Set param name
     * @return Param name
     */
    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Get / Set param value
     * @return Param value
     */
    public Object getValue() { return value; }
    public void setValue(Object value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return getName()+"="+String.valueOf(getValue());
    }
}