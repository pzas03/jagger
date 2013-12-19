package com.griddynamics.jagger.databaseapi.entity;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/5/13
 * Time: 12:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetricEntity {
    private String metricId;
    private String displayName;
    private Double summaryValue;

    public String getMetricId() {
        return metricId;
    }

    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Double getSummaryValue() {
        return summaryValue;
    }

    public void setSummaryValue(Double summaryValue) {
        this.summaryValue = summaryValue;
    }
}
