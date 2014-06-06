package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.ui.*;
import com.googlecode.gflot.client.Pan;
import com.googlecode.gflot.client.SimplePlot;

/**
 * class that represents plot with all its functionality */
public class PlotRepresentation extends VerticalPanel {

    private FlowPanel zoomPanel;
    private SimplePlot simplePlot;
    private Label xLabel;

    private MyScroll scrollbar;

    private final int TOTAL_BORDERS_WIDTH = 4;
    /**
     * Range to scroll to */
    private double maxRange = 1;

    public PlotRepresentation(FlowPanel zoomPanel, final SimplePlot simplePlot, Label xLabel) {
        super();
        this.zoomPanel = zoomPanel;
        this.simplePlot = simplePlot;
        this.xLabel = xLabel;
        this.add(zoomPanel);
        this.add(simplePlot);

        HorizontalPanel hp = new HorizontalPanel();
        hp.setWidth("100%");

        scrollbar = new MyScroll();
        scrollbar.setWidth("100%");
        scrollbar.setVisible(false);
        scrollbar.setHorizontalScrollPosition(0);

        // simple div
        SimplePanel sp = new SimplePanel();
        sp.setVisible(false);
        hp.add(sp);
        hp.setCellWidth(sp, simplePlot.getOptions().getYAxisOptions().getLabelWidth().intValue() + "px");
        hp.add(scrollbar);

        this.add(hp);
        this.add(xLabel);
        this.setWidth("100%");
        simplePlot.setSize("100%", "100%");
    }

    public double getMaxRange() {
        return maxRange;
    }

    public void calculateScrollWidth() {
        if (scrollbar.isAttached()) {
            double plotWidth = getPlotWidth() - TOTAL_BORDERS_WIDTH;
            double visibleRange = getVisibleRange();
            double ratio = maxRange / visibleRange;
            scrollbar.setScrollWidth((int) (plotWidth * ratio));
        }
    }

    public void setMaxRange(double maxRange) {
        this.maxRange = maxRange;
        calculateScrollWidth();
    }

    public MyScroll getScrollbar() {
        return scrollbar;
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

    public void panToPercent(double percent) {

        double minVisible = simplePlot.getAxes().getX().getMinimumValue();
        double visibleRange = getVisibleRange();

        double valueOnScaleShouldBe = percent * (maxRange - visibleRange);

        double deltaInScale = valueOnScaleShouldBe - minVisible;
        double pixelsToScale = getPlotWidth() / visibleRange;

        Pan pan = Pan.create().setLeft(deltaInScale * pixelsToScale).setPreventEvent(true);
        simplePlot.pan(pan);

        int newHorizontalPosition = (int) ((scrollbar.getMaximumHorizontalScrollPosition() - scrollbar.getMinimumHorizontalScrollPosition()) * percent);
        if (newHorizontalPosition == scrollbar.getHorizontalScrollPosition()) {
            // fire scroll event anyway
            NativeEvent event = Document.get().createScrollEvent();
            DomEvent.fireNativeEvent(event, scrollbar);
        } else {
            scrollbar.setHorizontalScrollPosition(newHorizontalPosition);
        }
    }

    private double getPlotWidth() {
        return simplePlot.getOffsetWidth() - simplePlot.getOptions().getYAxisOptions().getLabelWidth();
    }

    private double getVisibleRange() {
        return simplePlot.getAxes().getX().getMaximumValue() - simplePlot.getAxes().getX().getMinimumValue();
    }
}
