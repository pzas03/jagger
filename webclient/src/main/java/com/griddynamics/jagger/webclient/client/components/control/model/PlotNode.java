package com.griddynamics.jagger.webclient.client.components.control.model;

import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 12/2/13
 */
public class PlotNode extends SimpleNode {

    private PlotNameDto plotName;

    public PlotNode (PlotNameDto metricName) {
        this.plotName = metricName;
    }

    public PlotNode() {}

    public PlotNameDto getPlotName() {
        return plotName;
    }

    public void setPlotName(PlotNameDto plotName) {
        this.plotName = plotName;
    }

    @Override
    public List<? extends SimpleNode> getChildren() {
        return Collections.EMPTY_LIST;
    }
}
