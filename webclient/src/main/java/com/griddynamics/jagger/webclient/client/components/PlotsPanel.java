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

        layoutPanel.setChildHeight(plotContainerHeight);
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
                avalancheScrollEventsCount ++;
                plotContainer.getPlotRepresentation().panToPercent(percent);
            }
        });

        layoutPanel.setAdditionalHeightForChild(
                plotContainer.getDragPanelHeight()
                + plotContainer.getPlotRepresentation().getZoomPanelHeight()
                + plotContainer.getPlotRepresentation().getScrollPanelHeight()
                + plotContainer.getPlotRepresentation().getXAxisLabelHeight()
        );
        layoutPanel.addChild(plotContainer);
        childrenCount = layoutPanel.getAllChildren().size();
        setMaxRange();
    }

    private void panAllPlots(double percent) {

        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            pc.getPlotRepresentation().panToPercent(percent);
        }
    }


    private void scrollCalculations(final PlotContainer plotContainer) {

        double maxX = calculateMaxXAxisValue(plotContainer.getPlotRepresentation().getSimplePlot());

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

                avalancheScrollEventsCount = childrenCount;
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
    private int avalancheScrollEventsCount = 0;

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
     * Zoom all plots in PlotsPanel */
    public void zoomIn() {

       zoom(false);
    }

    /**
     * Zoom out all plots in PlotsPanel */
    public void zoomOut() {

       zoom(true);
    }

    /**
     * Zoom in if param is false, zoom out otherwise.
     * @param out defines whether zoom in or out.
     */
    private void zoom(boolean out) {

        double maxRange = layoutPanel.getFirstChild().getPlotRepresentation().getMaxRange();
        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            SimplePlot plot = pc.getPlotRepresentation().getSimplePlot();
            Zoom zoom = Zoom.create().setAmount(1.1);

            if (out) {
                plot.zoomOut(zoom);
            } else {
                plot.zoom(zoom);
            }

            pc.getPlotRepresentation().calculateScrollWidth();
        }

        PlotRepresentation plotRepresentation = layoutPanel.getFirstChild().getPlotRepresentation();
        double percent;
        double minVisible = plotRepresentation.getSimplePlot().getAxes().getX().getMinimumValue();
        double maxVisible = plotRepresentation.getSimplePlot().getAxes().getX().getMaximumValue();

        if (out) {
            if (maxVisible >= maxRange && minVisible <= 0) {
                // do nothing when plot in visible range
                return;
            }

            if (maxVisible >= maxRange) {
                // to the end
                plotRepresentation.panToPercent(1);
                return;
            } else if (minVisible <= 0) {
                // to very start
                plotRepresentation.panToPercent(0);
                return;
            }
        }

        percent = minVisible / (maxRange - maxVisible + minVisible);
        plotRepresentation.panToPercent(percent);
    }

    /**
     * Zoom to size of given plot;
     * @param plot - given plot */
    public void zoomDefault(SimplePlot plot) {

        double xMaxValue = calculateMaxXAxisValue(plot);

        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            SimplePlot currentPlot = pc.getPlotRepresentation().getSimplePlot();
            // currently we always start xAxis with zero
            currentPlot.getOptions().getXAxisOptions().setMinimum(0).setMaximum(xMaxValue);
            currentPlot.setupGrid();
            currentPlot.redraw();
            pc.getPlotRepresentation().calculateScrollWidth();
        }

        // all plots start with zero
        PlotRepresentation plotRepresentation = layoutPanel.getFirstChild().getPlotRepresentation();
        plotRepresentation.panToPercent(0);
    }


    /**
     * Returns max X axis value on plot
     * @param plot plot
     * @return max X axis value */
    private double calculateMaxXAxisValue (SimplePlot plot) {

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
        return maxValue;
    }

    /**
     * Check if PlotsPanel contains any plots.
     * @return true if it is empty, false otherwise */
    public boolean isEmpty() {
        return childrenCount == 0;
    }


    /**
     * @return maximum X axis value */
    public double getMaxXAxisValue() {
        // no widgets in panel
        assert layoutPanel.getWidgetCount() > 0;

        double xMaxValue = Double.MIN_VALUE;
        for (PlotContainer pc : layoutPanel.getAllChildren()) {

            double curveMaxX = calculateMaxXAxisValue(pc.getPlotRepresentation().getSimplePlot());

            if (curveMaxX > xMaxValue) {
                xMaxValue = curveMaxX;
            }
        }

        return xMaxValue;
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
            layoutPanel.changeChildrenHeight(plotContainerHeight);
        }
    }
}
