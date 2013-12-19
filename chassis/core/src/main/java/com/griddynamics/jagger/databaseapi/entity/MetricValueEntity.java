package com.griddynamics.jagger.databaseapi.entity;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/5/13
 * Time: 12:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetricValueEntity {
    private Long timeStamp;
    private Number value;

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }
}
