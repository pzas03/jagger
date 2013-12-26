package com.griddynamics.jagger.webclient.client.components.control.model;

import java.util.Collections;
import java.util.List;

/**
 * User: amikryukov
 * Date: 12/11/13
 */
public class MonitoringPlotNode extends AbstractIdentifyNode {

    private List<PlotNode> plots;

    public List<PlotNode> getPlots() {
        return plots;
    }

    public void setPlots(List<PlotNode> plots) {
        this.plots = plots;
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        return plots == null ? Collections.EMPTY_LIST : plots;
    }
}
