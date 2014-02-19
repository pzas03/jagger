package com.griddynamics.jagger.webclient.client.components.control.model;

import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;

import java.util.Collections;
import java.util.List;

/**
 * Corresponds to plots on "Metric" tab
 * User: amikryukov
 * Date: 12/2/13
 */
public class PlotNode extends AbstractIdentifyNode {

    private MetricNameDto metricNameDto;

    public PlotNode (MetricNameDto metricName) {
        this.metricNameDto = metricName;
    }

    public PlotNode() {}

    public MetricNameDto getPlotNameDto() {
        return metricNameDto;
    }

    public void setPlotNameDto(MetricNameDto metricNameDto) {
        this.metricNameDto = metricNameDto;
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        return Collections.EMPTY_LIST;
    }
}
