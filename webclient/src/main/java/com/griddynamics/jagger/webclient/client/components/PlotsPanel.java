package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Composite;
import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.SeriesData;
import com.googlecode.gflot.client.SimplePlot;
import com.googlecode.gflot.client.Zoom;
import com.sencha.gxt.widget.core.client.Slider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

/**
 * Class that hold widgets of type PlotContainer with dynamic layout feature.
 */
public class PlotsPanel extends Composite {

    interface PlotsPanelUiBinder extends UiBinder<Widget, PlotsPanel> {
    }

    private static PlotsPanelUiBinder ourUiBinder = GWT.create(PlotsPanelUiBinder.class);

    @UiField
    /* Main layout panel where all children will be */
    protected DynamicLayoutPanel<PlotContainer> layoutPanel;

    @UiField
    // temporary layout control panel todo: decide and implement final view of layout control.
    protected HorizontalPanel buttonPanel;

    /**
     * default value for container height */
    private Integer plotContainerHeight = 150;

    private ControlTree<String> controlTree;

    public PlotsPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));

        setUpButtonPanel();
    }

    public void setControlTree(ControlTree<String> controlTree) {
        this.controlTree = controlTree;
    }

    /**
     * Temporary solution of question how to change layout.
     */
    private void setUpButtonPanel() {

        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

        TextButton changeLayout = new TextButton("Change layout");
        changeLayout.addSelectHandler(new ChangeLayoutHandler());

        TextButton oneColumnLButton = new TextButton("One column");
        oneColumnLButton.addSelectHandler(new OneColumnLHandler());

        TextButton twoColumnsLButton = new TextButton("Two columns");
        twoColumnsLButton.addSelectHandler(new TwoColumnsHandler());

        Slider heightSlider = new Slider();
        heightSlider.setValue(plotContainerHeight);
        heightSlider.setMaxValue(500);
        heightSlider.setMinValue(100);
        heightSlider.addValueChangeHandler(new HeightSliderValueChangeHandler());

        buttonPanel.add(changeLayout);
        buttonPanel.add(oneColumnLButton);
        buttonPanel.add(twoColumnsLButton);
        buttonPanel.add(heightSlider);

    }

    /**
     * Remove widget from layoutPanel by element id
     * @param elementId Id of widget element (Widget.getElement.getId())*/
    public void removeElementById(String elementId) {
        layoutPanel.removeChild(elementId);
        childrenCount = layoutPanel.getAllChildren().size();
        setMaxRange();
        controlTree.setCheckState(elementId, Tree.CheckState.UNCHECKED);
    }

    /**
     * Remove all widgets from layoutPanel */
    public void clear() {
        layoutPanel.clear();
        childrenCount = 0;
    }

    /**
     * Add widget to layoutPanel
     * @param plotContainer child widget */
    public void addElement(final PlotContainer plotContainer) {
        plotContainer.setHeight(plotContainerHeight + "px");
        plotContainer.setPlotsPanel(this);
        scrollCalculations(plotContainer);

        plotContainer.getPlotRepresentation().addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                // executes when plot have been loaded
                plotContainer.getPlotRepresentation().calculateScrollWidth();
                panOnePlot(plotContainer.getPlotRepresentation(), percent);
            }
        });

        layoutPanel.addChild(plotContainer);
        childrenCount = layoutPanel.getAllChildren().size();
        setMaxRange();

        // do not fire calculating of percent for new plot
        panOnePlot(plotContainer.getPlotRepresentation(), percent);
    }

    private void panAllPlots(double percent) {

        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            avalancheScrollEventsCount ++;
            pc.getPlotRepresentation().panToPercent(percent);
        }
        avalancheScrollEventsCount --;
    }


    private void scrollCalculations(final PlotContainer plotContainer) {

        double maxX = Double.MIN_VALUE;
        JsArray<Series> series = plotContainer.getPlotRepresentation().getSimplePlot().getModel().getSeries();
        for (int i = 0; i < series.length(); i++) {
            Series s = series.get(i);
            SeriesData sd = s.getData();
            double curMax = sd.getX(sd.length() - 1);
            if (maxX < curMax) {
                maxX = curMax;
            }
        }

        double maxRange;
        if (this.isEmpty()) {
            maxRange = maxX;
        } else {
            if (maxX > getMaxXAxisValue()) {
                maxRange = maxX;
            } else {
                maxRange = getMaxXAxisValue();
            }
        }

        plotContainer.getPlotRepresentation().setMaxRange(maxRange);

        final NativeHorizontalScrollbar scrollBar = plotContainer.getPlotRepresentation().getScrollbar();
        scrollBar.setVisible(true);
        scrollBar.addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {

                if (avalancheScrollEventsCount > 0) {
                    avalancheScrollEventsCount --;
                    return;
                }

                int currentPosition = scrollBar.getHorizontalScrollPosition();
                double percent = 1D * (currentPosition - scrollBar.getMinimumHorizontalScrollPosition()) /
                        (scrollBar.getMaximumHorizontalScrollPosition() - scrollBar.getMinimumHorizontalScrollPosition());
                PlotsPanel.this.percent = percent;
                panAllPlots(percent);
            }
        });

    }

    /**
     * Global counter to see how many avalanche scroll events hav not been finished */
    int avalancheScrollEventsCount = 0;

    /**
     * To avoid calculating on every scroll  event */
    private int childrenCount = 0;

    /**
     * Current state of plot`s scrolls */
    private double percent = 0;

    private void setMaxRange() {
        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            pc.getPlotRepresentation().setMaxRange(getMaxXAxisValue());
        }
    }


    /**
     * Check if PlotsPanel contains element with certain id
     * @param plotId id of element to identify
     * @return true if element found with given plotId, false otherwise */
    public boolean containsElementWithId(String plotId) {
        return layoutPanel.containsElementWithId(plotId);
    }


    /**
     * boolean to disable ZoomOut when visible range of plot more then maximum range */
    boolean zoomOutEnabled = true;

    /**
     * Zoom all plots in PlotsPanel */
    public void zoomIn() {

        if (!zoomOutEnabled) {
            zoomOutEnabled = true;
        }

        double maxRange = layoutPanel.getFirstChild().getPlotRepresentation().getMaxRange();
        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            SimplePlot plot = pc.getPlotRepresentation().getSimplePlot();
            Zoom zoom = Zoom.create().setAmount(1.1);
            plot.zoom(zoom);

            pc.getPlotRepresentation().calculateScrollWidth();
        }

        PlotRepresentation plotRepresentation = layoutPanel.getFirstChild().getPlotRepresentation();
        double percent;
        double minVisible = plotRepresentation.getSimplePlot().getAxes().getX().getMinimumValue();
        double maxVisible = plotRepresentation.getSimplePlot().getAxes().getX().getMaximumValue();

        percent = minVisible / (maxRange - maxVisible + minVisible);

        panAllPlots(percent);
    }

    /**
     * Zoom out all plots in PlotsPanel */
    public void zoomOut() {
        if (!zoomOutEnabled)
            return;

        double maxRange = layoutPanel.getFirstChild().getPlotRepresentation().getMaxRange();
        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            SimplePlot plot = pc.getPlotRepresentation().getSimplePlot();
            Zoom zoom = Zoom.create().setAmount(1.1);
            plot.zoomOut(zoom);
            pc.getPlotRepresentation().calculateScrollWidth();
        }

        PlotRepresentation plotRepresentation = layoutPanel.getFirstChild().getPlotRepresentation();
        double minVisible = plotRepresentation.getSimplePlot().getAxes().getX().getMinimumValue();
        double maxVisible = plotRepresentation.getSimplePlot().getAxes().getX().getMaximumValue();

        double percent;
        if (maxVisible >= maxRange) {
            percent = 1;
        } else if (minVisible <= 0) {
            percent = 0;
        } else {
            percent = minVisible / (maxRange - maxVisible + minVisible);
        }

        panAllPlots(percent);
        if (maxVisible - minVisible >= maxRange)
            zoomOutEnabled = false;
    }

    /**
     * Zoom to size of given plot;
     * @param plot - given plot */
    public void zoomBackTo(SimplePlot plot) {

        JsArray<Series> seriesArray = plot.getModel().getSeries();
        double maxValue = Double.MIN_VALUE;
        for (int i = 0; i < seriesArray.length(); i ++) {
            // get curve
            SeriesData curve = seriesArray.get(i).getData();
            double temp = curve.getX(curve.length() - 1);
            if (maxValue < temp) {
                maxValue = temp;
            }
        }

        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            SimplePlot currentPlot = pc.getPlotRepresentation().getSimplePlot();
            // currently we always start xAxis with zero
            currentPlot.getOptions().getXAxisOptions().setMinimum(0).setMaximum(maxValue);
            currentPlot.setupGrid();
            currentPlot.redraw();
            pc.getPlotRepresentation().calculateScrollWidth();
        }

        // all plots start with zero
        panAllPlots(0);
    }

    private void panOnePlot(PlotRepresentation plotRepresentation, double percent) {
        avalancheScrollEventsCount ++;
        plotRepresentation.panToPercent(percent);
    }


    /**
     * Check if PlotsPanel contains any plots.
     * @return true if it is empty, false otherwise */
    public boolean isEmpty() {
        return layoutPanel.getWidgetCount() == 0;
    }


    /**
     * @return maximum X axis value */
    public double getMaxXAxisValue() {
        // no widgets in panel
        assert layoutPanel.getWidgetCount() > 0;

        double maxValue = Integer.MIN_VALUE;
        for (PlotContainer pc : layoutPanel.getAllChildren()) {

            JsArray<Series> curves = pc.getPlotRepresentation().getSimplePlot().getModel().getSeries();
            for (int i = 0; i < curves.length(); i ++) {
                Series curve = curves.get(i);
                double curveMaxX = curve.getData().getX(curve.getData().length() - 1);
                if (curveMaxX > maxValue) {
                    maxValue = curveMaxX;
                }
            }
        }

        return maxValue;
    }

    /**
     * @return maximum visible X axis value */
    public double getMaxXAxisVisibleValue() {
        // no widgets in panel
        assert layoutPanel.getWidgetCount() > 0;

        SimplePlot plot = layoutPanel.getFirstChild().getPlotRepresentation().getSimplePlot();
        return plot.getAxes().getX().getMaximumValue();
    }


    /**
     * @return minimum visible X axis value */
    public double getMinXAxisVisibleValue() {
        // no widgets in panel
        assert layoutPanel.getWidgetCount() > 0;

        SimplePlot plot = layoutPanel.getFirstChild().getPlotRepresentation().getSimplePlot();
        return plot.getAxes().getX().getMinimumValue();
    }

    private class ChangeLayoutHandler implements SelectEvent.SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            layoutPanel.changeLayout(layoutPanel.getLayout().getNext());
        }
    }

    private class OneColumnLHandler implements SelectEvent.SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            layoutPanel.changeLayout(DynamicLayoutPanel.Layout.ONE_COLUMN);
        }
    }

    private class TwoColumnsHandler implements SelectEvent.SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            layoutPanel.changeLayout(DynamicLayoutPanel.Layout.TWO_COLUMNS);
        }
    }

    private class HeightSliderValueChangeHandler implements ValueChangeHandler<Integer> {
        @Override
        public void onValueChange(ValueChangeEvent<Integer> integerValueChangeEvent) {
            plotContainerHeight = integerValueChangeEvent.getValue();
            layoutPanel.changeChildrenHeight(plotContainerHeight + "px");
        }
    }
}
