package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.event.logical.shared.AttachEvent;
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

    /**
     * Plot know where should it be. It is useful when change layouts for example*/
    double percent = 0;

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

        SimplePanel sp = new SimplePanel(); sp.setVisible(false);
        hp.add(sp);
        hp.setCellWidth(sp, "40px");
        hp.add(scrollbar);

        this.add(hp);
        this.add(xLabel);
        this.setWidth("100%");
        simplePlot.setSize("100%", "100%");

        addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                // executes when plot have been loaded

                calculateScrollWidth();
                panToPercent(percent);
            }
        });
    }

    public double getMaxRange() {
        return maxRange;
    }

    public void calculateScrollWidth() {
        if (scrollbar.isAttached()) {
            double plotWidth = simplePlot.getOffsetWidth() - simplePlot.getOptions().getYAxisOptions().getLabelWidth() - 4;
            double visibleRange = simplePlot.getAxes().getX().getMaximumValue() - simplePlot.getAxes().getX().getMinimumValue();
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

        if (this.percent == percent)
            return;

        double minVisible = simplePlot.getAxes().getX().getMinimumValue();
        double maxVisible = simplePlot.getAxes().getX().getMaximumValue();

        double valueOnScaleShouldBe = percent * (maxRange - maxVisible + minVisible);

        double deltaInScale = valueOnScaleShouldBe - minVisible;
        double pixelsToScale = (simplePlot.getOffsetWidth() - simplePlot.getOptions().getYAxisOptions().getLabelWidth()) / (maxVisible - minVisible);

        Pan pan = Pan.create().setLeft(deltaInScale * pixelsToScale).setPreventEvent(true);
        simplePlot.pan(pan);

        scrollbar.setHorizontalScrollPosition((int) ((scrollbar.getMaximumHorizontalScrollPosition() - scrollbar.getMinimumHorizontalScrollPosition()) * percent));
        this.percent = percent;
    }
}
