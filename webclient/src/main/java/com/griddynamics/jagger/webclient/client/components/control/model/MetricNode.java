package com.griddynamics.jagger.webclient.client.components.control.model;

import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/26/13
 */
public class MetricNode extends SimpleNode {

    private MetricNameDto metricName;

    public MetricNode (MetricNameDto metricName) {
        this.metricName = metricName;
    }

    public MetricNode() {}

    public MetricNameDto getMetricName() {
        return metricName;
    }

    public void setMetricName(MetricNameDto metricName) {
        this.metricName = metricName;
    }

    @Override
    public List<? extends SimpleNode> getChildren() {
        return Collections.EMPTY_LIST;
    }

}
