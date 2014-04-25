package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.sencha.gxt.widget.core.client.Slider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

public class PlotsPanel extends Composite {

    interface PlotsPanelUiBinder extends UiBinder<Widget, PlotsPanel> {
    }

    private static PlotsPanelUiBinder ourUiBinder = GWT.create(PlotsPanelUiBinder.class);

    @UiField
    /* Main layout panel where all children will be */
    protected DynamicLayoutPanel layoutPanel;

    @UiField
    protected HorizontalPanel buttonPanel;

    /**
     * default value for container height */
    private Integer plotContainerHeight = 150;

    public PlotsPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));

        setUpButtonPanel();
        setUpLayoutPanel();
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

    private void setUpLayoutPanel() {
    }


    /**
     * Remove widget from layoutPanel by element id
     * @param elementId Id of widget element (Widget.getElement.getId())*/
    public void removeElementById(String elementId) {
        layoutPanel.removeChild(elementId);
    }

    /**
     * Remove all widgets from layoutPanel */
    public void clear() {
        layoutPanel.clear();
    }

    /**
     * Add widget to layoutPanel
     * @param w child widget */
    public void addElement(Widget w) {
        w.setHeight(plotContainerHeight + "px");
        layoutPanel.addChild(w);
    }

    public boolean containsElementWithId(String plotId) {
        return layoutPanel.containsElementWithId(plotId);
    }

    private class ChangeLayoutHandler implements SelectEvent.SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            int size = DynamicLayoutPanel.Layout.values().length;
            DynamicLayoutPanel.Layout layout = DynamicLayoutPanel.Layout.values()[(layoutPanel.getLayout().ordinal() + 1) % size];
            layoutPanel.changeLayout(layout);
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
