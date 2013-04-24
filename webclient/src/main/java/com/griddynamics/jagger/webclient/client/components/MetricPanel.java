package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 05.04.13
 * Time: 12:09
 * To change this template use File | Settings | File Templates.
 */
public class MetricPanel extends Composite {
    interface MetricPanelUiBinder extends UiBinder<Widget, MetricPanel> {
    }

    private static MetricPanelUiBinder ourUiBinder = GWT.create(MetricPanelUiBinder.class);

    @UiField(provided = true)
    CellTree tree;

    ListDataProvider<TaskDataDto> provider = new ListDataProvider<TaskDataDto>();
    final MultiSelectionModel selectionModel = new MultiSelectionModel<MetricNameDto>();

    public MetricPanel() {
        tree = new CellTree(new MetricModel(selectionModel, provider), null);
        tree.setTitle("Metrics");

        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void updateTests(Set<TaskDataDto> tests){

        provider.setList(Arrays.asList((TaskDataDto)null));

        if (tests.size()==0){
            return;
        }

        boolean manySessions = false;
        for (TaskDataDto test : tests){
            if (test.getIds().size() > 1){
                manySessions = true;
                break;
            }
        }

        if (!manySessions){
            //nothing to show
            return;
        }

        provider.setList(new ArrayList<TaskDataDto>(tests));
    }

    public Set<MetricNameDto> getSelected(){
        return selectionModel.getSelectedSet();
    }

    public void addSelectionListener(SelectionChangeEvent.Handler handler){
        selectionModel.addSelectionChangeHandler(handler);
    }
}