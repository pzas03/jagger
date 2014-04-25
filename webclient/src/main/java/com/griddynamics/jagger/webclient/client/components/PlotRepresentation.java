package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.gflot.client.SimplePlot;

/**
 * class that represents plot with all its functionality */
public class PlotRepresentation extends VerticalPanel {

    private Label plotHeader;
    private FlowPanel zoomPanel;
    private SimplePlot simplePlot;
    private Label xLabel;

    public PlotRepresentation(Label plotHeader, FlowPanel zoomPanel, SimplePlot simplePlot, Label xLabel) {
        super();
        this.plotHeader = plotHeader;
        this.zoomPanel = zoomPanel;
        this.simplePlot = simplePlot;
        this.xLabel = xLabel;
        this.add(plotHeader);
        this.add(zoomPanel);
        this.add(simplePlot);
        this.add(xLabel);
        this.setWidth("100%");
        simplePlot.setSize("100%", "100%");
    }

    public FlowPanel getZoomPanel() {
        return zoomPanel;
    }

    public SimplePlot getSimplePlot() {
        return simplePlot;
    }

    public Label getxLabel() {
        return xLabel;
    }

    public Label getPlotHeader() {
        return plotHeader;
    }
}
