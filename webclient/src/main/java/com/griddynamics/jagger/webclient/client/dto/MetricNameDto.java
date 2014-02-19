package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 08.04.13
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */
public class MetricNameDto implements Serializable {

    private TaskDataDto tests;
    private String metricName;
    private String metricDisplayName;

    public MetricNameDto() {
    }

    public MetricNameDto(TaskDataDto tests, String metricName) {
        this.tests = tests;
        this.metricName = metricName;
    }

    public long getTaskId() {
        if (tests.getIds() == null || tests.getIds().size() != 1) {
            throw new UnsupportedOperationException("Cannot return id because of ids is null or its size is not equal 1");
        }
        return tests.getIds().iterator().next();
    }

    public Set<Long> getTaskIds() {
        return tests.getIds();
    }


    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public TaskDataDto getTests() {
        return tests;
    }

    public void setTests(TaskDataDto tests) {
        this.tests = tests;
    }

    public String getMetricDisplayName() {
        return metricDisplayName == null ? metricName : metricDisplayName;
    }

    public void setMetricDisplayName(String metricDisplayName) {
        this.metricDisplayName = metricDisplayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricNameDto that = (MetricNameDto) o;

        if (metricName != null ? !metricName.equals(that.metricName) : that.metricName != null) return false;
        if (tests != null ? !tests.equals(that.tests) : that.tests != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tests != null ? tests.hashCode() : 0;
        result = 31 * result + (metricName != null ? metricName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MetricNameDto{" +
                "tests=" + tests +
                ", metricName='" + metricName + '\'' +
                '}';
    }
}
