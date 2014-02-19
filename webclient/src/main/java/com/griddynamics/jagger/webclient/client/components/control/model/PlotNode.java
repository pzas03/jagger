package com.griddynamics.jagger.webclient.client.components.control.model;

import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;

import java.util.Collections;
import java.util.List;

/**
 * Corresponds to plots on "Metric" tab
 * User: amikryukov
 * Date: 12/2/13
 */
public class PlotNode extends AbstractIdentifyNode {

    private PlotNameDto plotNameDto;

    public PlotNode (PlotNameDto metricName) {
        this.plotNameDto = metricName;
    }

    public PlotNode() {}

    public PlotNameDto getPlotNameDto() {
        return plotNameDto;
    }

    public void setPlotNameDto(PlotNameDto plotNameDto) {
        this.plotNameDto = plotNameDto;
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        return Collections.EMPTY_LIST;
    }
}
