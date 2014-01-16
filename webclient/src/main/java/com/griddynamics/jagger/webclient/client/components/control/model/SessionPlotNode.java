package com.griddynamics.jagger.webclient.client.components.control.model;

import com.griddynamics.jagger.webclient.client.dto.SessionPlotNameDto;

import java.util.Collections;
import java.util.List;

/**
 * User: amikryukov
 * Date: 12/4/13
 */
public class SessionPlotNode extends AbstractIdentifyNode {
    private SessionPlotNameDto plotNameDto;

    public SessionPlotNode (SessionPlotNameDto metricName) {
        this.plotNameDto = metricName;
    }

    public SessionPlotNode() {}

    public SessionPlotNameDto getPlotNameDto() {
        return plotNameDto;
    }

    public void setPlotNameDto(SessionPlotNameDto plotNameDto) {
        this.plotNameDto = plotNameDto;
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        return Collections.EMPTY_LIST;
    }
}
