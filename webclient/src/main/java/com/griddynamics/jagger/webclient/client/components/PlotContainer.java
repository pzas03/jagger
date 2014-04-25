package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.sencha.gxt.dnd.core.client.*;


/**
 * Draggable container that holds plot representation */
public class PlotContainer extends VerticalPanel {

    private PlotRepresentation chart;

    public PlotContainer(String id, PlotRepresentation chart) {
        super();
        this.getElement().setId(id);
        this.chart = chart;
        initContainer();
    }

    private void initDragAndDropBehavior(final Widget dragSource) {

        DropTarget target = new DropTarget(this) {
            @Override
            protected void onDragDrop(DndDropEvent event) {
                super.onDragDrop(event);
                PlotContainer incoming = (PlotContainer) event.getData();
                PlotContainer current = (PlotContainer) component;
                swap(incoming, current);
            }
        };

        target.setFeedback(DND.Feedback.BOTH);


        new DragSource(dragSource) {
            @Override
            protected void onDragStart(DndDragStartEvent event) {
                super.onDragStart(event);
                // by default drag is allowed
                event.setData(dragSource.getParent());
            }
        };
    }

    // swap data of containers
    private void swap(PlotContainer c1, PlotContainer c2) {
        String id1 = c1.getElement().getId();
        String id2 = c2.getElement().getId();

        PlotRepresentation ch1 = c1.getChart();
        PlotRepresentation ch2 = c2.getChart();

        c1.getElement().setId(id2);
        c2.getElement().setId(id1);
        c1.setChart(ch2);
        c2.setChart(ch1);
    }

    private void initContainer() {
        this.setWidth("100%");
        SimplePanel dragPanel = new SimplePanel();
        dragPanel.setSize("100%", "30px");
        dragPanel.addStyleName(JaggerResources.INSTANCE.css().dragLabel());

        add(dragPanel);
        add(chart);

        initDragAndDropBehavior(dragPanel);
    }

    public void setChart(PlotRepresentation chart) {
        remove(this.chart);
        this.chart = chart;
        add(this.chart);
    }

    public PlotRepresentation getChart() {
        return chart;
    }

    @Override
    public void setHeight(String height) {
        chart.getSimplePlot().setHeight(height);
    }
}