package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.googlecode.gflot.client.Pan;
import com.googlecode.gflot.client.SimplePlot;
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
    /* Menu bar to control plot panel */
    protected PlotButtonsPanel plotButtonsPanel;

    @UiField
    /* Scroll bar for layout panel */
    protected ScrollPanel scrollPanelMetrics;

    private ControlTree<String> controlTree;

    public PlotsPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));

        plotButtonsPanel.setupButtonPanel(this);
    }

    public void setControlTree(ControlTree<String> controlTree) {
        this.controlTree = controlTree;
    }

    /**
     * Remove widget from layoutPanel by element id
     * @param elementId Id of widget element (Widget.getElement.getId())*/
    public void removeElementById(String elementId) {
        layoutPanel.removeChild(elementId);
        controlTree.setCheckState(elementId, Tree.CheckState.UNCHECKED);
    }

    /**
     * Remove all widgets from layoutPanel */
    public void clear() {
        layoutPanel.clear();
    }

    /**
     * Add widget to layoutPanel
     * @param plotContainer child widget */
    public void addElement(PlotContainer plotContainer) {
        plotContainer.setHeight(plotButtonsPanel.getPlotHeight() + "px");
        plotContainer.setPlotsPanel(this);
        layoutPanel.addChild(plotContainer);
    }

    /**
     * Check if PlotsPanel contains element with certain id
     * @param plotId id of element to identify
     * @return true if element found with given plotId, false otherwise */
    public boolean containsElementWithId(String plotId) {
        return layoutPanel.containsElementWithId(plotId);
    }


    /**
     * Pan All plots that contains in PlotsPanel
     * @param pan amount to pan left */
    public void panAllPlots(int pan) {
        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            SimplePlot plot = pc.getPlotRepresentation().getSimplePlot();
            plot.pan(Pan.create().setLeft(pan));
        }
    }


    /**
     * Zoom all plots in PlotsPanel */
    public void zoomIn() {
        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            SimplePlot plot = pc.getPlotRepresentation().getSimplePlot();
            plot.zoom();
        }
    }

    /**
     * Zoom out all plots in PlotsPanel */
    public void zoomOut() {
        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            SimplePlot plot = pc.getPlotRepresentation().getSimplePlot();
            plot.zoomOut();
        }
    }

    /**
     * Check if PlotsPanel contains any plots.
     * @return true if it is empty, false otherwise */
    public boolean isEmpty() {
        return layoutPanel.getWidgetCount() == 0;
    }

    /**
     * @return minimum X axis value */
    public double getMinXAxisValue() {
        // no widgets in panel
        assert layoutPanel.getWidgetCount() > 0;

        SimplePlot plot = layoutPanel.getFirstChild().getPlotRepresentation().getSimplePlot();

        return plot.getAxes().getX().getMinimumValue();
    }

    /**
     * @return maximum X axis value */
    public double getMaxXAxisValue() {
        // no widgets in panel
        assert layoutPanel.getWidgetCount() > 0;

        SimplePlot plot = layoutPanel.getFirstChild().getPlotRepresentation().getSimplePlot();

        return plot.getAxes().getX().getMaximumValue();
    }

    /**
     * Change layout of plots (single columns, two columns) */
    public void changeLayout() {
        layoutPanel.changeLayout(layoutPanel.getLayout().getNext());
    }

    /**
     * Change height of plots */
    public void changeChildrenHeight(Integer height) {
        layoutPanel.changeChildrenHeight(height + "px");
    }

    /**
     * Scroll to bottom of layout panel (panel with plots) */
    public void scrollToBottom() {
        scrollPanelMetrics.scrollToBottom();
    }
}
