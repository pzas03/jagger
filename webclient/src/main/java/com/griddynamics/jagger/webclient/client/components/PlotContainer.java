package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.sencha.gxt.dnd.core.client.*;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;


/**
 * Draggable container that holds plot representation */
public class PlotContainer extends VerticalPanel {

    private PlotRepresentation plotRepresentation;

    private PlotSaver plotSaver;

    private PlotsPanel plotsPanel;

    private TextBox plotHeader;

    private HorizontalPanel dragPanel;

    public PlotContainer(String id, String plotHeader, PlotRepresentation chart) {
        super();
        this.getElement().setId(id);
        this.plotRepresentation = chart;
        this.plotHeader = new TextBox();
        this.plotHeader.setText(plotHeader);
        initContainer();
    }

    public PlotContainer(String id, String plotHeader, PlotRepresentation chart, PlotSaver plotSaver) {
        super();
        this.getElement().setId(id);
        this.plotRepresentation = chart;
        this.plotSaver = plotSaver;
        this.plotHeader = new TextBox();
        this.plotHeader.setText(plotHeader);
        initContainer();
    }

    private void initDragAndDropBehavior() {

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


        new DragSource(dragPanel) {
            @Override
            protected void onDragStart(DndDragStartEvent event) {
                super.onDragStart(event);
                // by default drag is allowed
                event.setData(dragPanel.getParent());
            }
        };
    }

    // swap data of containers
    private void swap(PlotContainer c1, PlotContainer c2) {
        String id1 = c1.getElement().getId();
        String id2 = c2.getElement().getId();

        String header1 = c1.getPlotHeaderText();
        String header2 = c2.getPlotHeaderText();

        PlotRepresentation ch1 = c1.getPlotRepresentation();
        PlotRepresentation ch2 = c2.getPlotRepresentation();

        c1.getElement().setId(id2);
        c2.getElement().setId(id1);
        c1.setPlotRepresentation(ch2);
        c2.setPlotRepresentation(ch1);
        c1.setPlotHeaderText(header2);
        c2.setPlotHeaderText(header1);
    }

    private void initContainer() {

        this.setWidth("100%");
        dragPanel = new HorizontalPanel();
        dragPanel.setVerticalAlignment(ALIGN_MIDDLE);
        dragPanel.addStyleName(JaggerResources.INSTANCE.css().dragLabel());
        dragPanel.addStyleName(JaggerResources.INSTANCE.css().draggable());
        dragPanel.add(plotHeader);
        plotHeader.setEnabled(false);
        plotHeader.setStyleName(JaggerResources.INSTANCE.css().plotHeader());
        plotHeader.addStyleName(JaggerResources.INSTANCE.css().draggable());

        // select all text in plot header on double click
        dragPanel.addDomHandler(new DoubleClickHandler() {

            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                plotHeader.selectAll();
            }
        }, DoubleClickEvent.getType());

        // menu creation
        final Menu settingsMenu = generateMenu();

        final Image settingsImageButton = new Image(JaggerResources.INSTANCE.getGearImage().getSafeUri());
        settingsImageButton.addStyleName(JaggerResources.INSTANCE.css().pointer());
        settingsImageButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                settingsMenu.show(settingsImageButton);
            }
        });


        // todo : JFG-759
        // It is not working for Treands in current solution, as ids of trends plot generating on metricNameDto object bases.
        final Image closeImageButton = new Image(JaggerResources.INSTANCE.getCrossImage().getSafeUri());
        closeImageButton.addStyleName(JaggerResources.INSTANCE.css().pointer());
        closeImageButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                plotsPanel.removeElementById(PlotContainer.this.getElement().getId());
            }
        });



        dragPanel.add(settingsImageButton);
        dragPanel.setCellWidth(settingsImageButton, "20px");

        // simple div in html
        SimplePanel separator = new SimplePanel();

        dragPanel.add(separator);
        dragPanel.setCellWidth(separator, "15px");

        dragPanel.add(closeImageButton);
        dragPanel.setCellWidth(closeImageButton, "20px");

        add(dragPanel);
        add(plotRepresentation);

        initDragAndDropBehavior();
    }

    private Menu generateMenu() {

        Menu settingsMenu = new Menu();
        settingsMenu.addStyleName(JaggerResources.INSTANCE.css().plotSettingsMenu());
        MenuItem saveMenuItem = new MenuItem("Save");
        if (plotSaver == null) {
            saveMenuItem.setEnabled(false);
        }
        saveMenuItem.addSelectionHandler(new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> event) {
                plotSaver.saveAsPng(
                        plotRepresentation.getSimplePlot(),
                        getPlotHeaderText(),
                        plotRepresentation.getxLabel().getText());
            }
        });
        if (plotSaver == null) {
            saveMenuItem.setEnabled(false);
        }
        saveMenuItem.setIcon(JaggerResources.INSTANCE.getDownloadImage());

        settingsMenu.add(saveMenuItem);

        return settingsMenu;
    }

    public void setPlotRepresentation(PlotRepresentation plotRepresentation) {
        remove(this.plotRepresentation);
        this.plotRepresentation = plotRepresentation;
        add(this.plotRepresentation);
    }

    public PlotRepresentation getPlotRepresentation() {
        return plotRepresentation;
    }

    private String getPlotHeaderText() {
        return this.plotHeader.getText();
    }

    private void setPlotHeaderText(String headerText) {
        this.plotHeader.setText(headerText);
    }

    public void setPlotsPanel(PlotsPanel plotsPanel) {
        this.plotsPanel = plotsPanel;
    }

    @Override
    public void setHeight(String height) {
        plotRepresentation.getSimplePlot().setHeight(height);
    }
}