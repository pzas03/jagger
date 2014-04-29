package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel that allows to represent child elements with one or two columns
 */
public class DynamicLayoutPanel extends VerticalPanel {

    private Layout layout = Layout.ONE_COLUMN;

    public Layout getLayout() {
        return layout;
    }

    private final Widget stub;

    {
        Label label = new Label();
        label.setPixelSize(0, 0);
        label.setVisible(false);
        this.stub = label;
    }

    public void changeLayout(Layout layout) {

        if (this.layout == layout) // no need to change anything
            return;

        this.layout = layout;

        List<Widget> widgets = new ArrayList<Widget>();
        for (int i = 0; i < getWidgetCount(); i++) {
            HorizontalPanel hp = (HorizontalPanel) getWidget(i);
            for (int j = 0; j < hp.getWidgetCount(); j++) {
                Widget widget = hp.getWidget(j);
                if (widget != stub)
                    widgets.add(widget);
            }
        }

        clear();

        for (Widget widget : widgets) {
            addChild(widget);
        }
    }

    public void addChild(Widget widget) {

        switch (layout) {
            case TWO_COLUMNS: addChildTwoColumns(widget);
                              break;

            default:          addChildOneColumn(widget);
        }
    }

    private void addChildOneColumn(Widget widget) {
        HorizontalPanel newHp = new HorizontalPanel();
        newHp.setHorizontalAlignment(ALIGN_CENTER);
        newHp.setWidth("100%");
        newHp.add(widget);
        newHp.setCellWidth(widget, "100%");
        newHp.setSpacing(1);
        this.add(newHp);
    }

    private void addChildTwoColumns(Widget widget) {

        int totalCount = this.getWidgetCount();
        if (totalCount != 0) {
            HorizontalPanel hp = (HorizontalPanel) getWidget(totalCount - 1); // get last horizontal Panel
            if (hp.getWidgetCount() == 2 && stub == hp.getWidget(1)) {
                hp.remove(stub);
                hp.add(widget);
                hp.setCellWidth(widget, "50%");
                return;
            }
        }

        HorizontalPanel newHp = new HorizontalPanel();
        newHp.setHorizontalAlignment(ALIGN_CENTER);
        newHp.setWidth("100%");
        newHp.add(widget);
        newHp.setCellWidth(widget, "50%");
        newHp.add(stub);
        newHp.setCellWidth(stub, "50%");
        newHp.setSpacing(1);
        this.add(newHp);
    }

    public void changeChildrenHeight(String plotContainerHeight) {
        for (int i = 0; i < getWidgetCount(); i ++) {
            HorizontalPanel hp = (HorizontalPanel) getWidget(i);
            for (int j = 0; j < hp.getWidgetCount(); j ++) {
                Widget widget = hp.getWidget(j);
                widget.setHeight(plotContainerHeight);
            }
        }
    }

    /**
     * @param id id of widget to remove (id of plot container)
     */
    public void removeChild(String id) {
        if (layout == Layout.ONE_COLUMN) {
            for (int i = 0; i < getWidgetCount(); i ++) {
                HorizontalPanel hp = (HorizontalPanel) getWidget(i);
                if (id.equals(hp.getWidget(0).getElement().getId())) {
                    remove(i);
                    return;
                }
            }
        } else {
            List<Widget> containers = new ArrayList<Widget>();
            for (int i = 0; i < getWidgetCount(); i ++) {
                HorizontalPanel hp = (HorizontalPanel) getWidget(i);
                for (int j = 0; j < hp.getWidgetCount(); j++) {
                    Widget pc = hp.getWidget(j);
                    if (!id.equals(pc.getElement().getId()) && pc != stub) {
                        containers.add(pc);
                    }
                }
            }
            clear();
            for (Widget pc : containers) {
                addChild(pc);
            }
        }
    }

    public boolean containsElementWithId(String plotId) {

        for (int i = 0; i < getWidgetCount(); i ++) {
            HorizontalPanel hp = (HorizontalPanel) getWidget(i);
            for (int j = 0; j < hp.getWidgetCount(); j++) {
                if (plotId.equals(hp.getWidget(j).getElement().getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Enum of possible layouts of DynamicLayoutPanel*/
    public static enum Layout {
        ONE_COLUMN , TWO_COLUMNS;

        /**
         * @return next value of Layout enum by circle */
        Layout getNext() {
            return Layout.values()[(this.ordinal() + 1) % Layout.values().length];
        }
    }
}
