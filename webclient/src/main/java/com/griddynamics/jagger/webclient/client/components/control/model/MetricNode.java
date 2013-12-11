package com.griddynamics.jagger.webclient.client.components.control.model;

import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;

import java.util.Collections;
import java.util.List;

/**
 * Corresponds to summary/trends metrics
 * User: amikryukov
 * Date: 11/26/13
 */
public class MetricNode extends AbstractIdentifyNode {

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
    public List<? extends AbstractIdentifyNode> getChildren() {
        return Collections.EMPTY_LIST;
    }

}
