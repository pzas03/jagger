package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.MetricNode;
import com.griddynamics.jagger.webclient.client.components.control.model.PlotNode;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 12/2/13
 */
public class PlotNodeHandler extends TreeAwareHandler<PlotNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<PlotNode> event) {

        //???
        //Set<MetricNameDto> metricNameDtos = new HashSet<MetricNameDto>();
        //metricNameDtos.addAll(event.getItem().getMetricNameDtoList());

        Set<MetricNode> metricNodeSet = new HashSet<MetricNode>();
        metricNodeSet.add(event.getItem());

        if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
            //???testPlotFetcher.fetchPlots(metricNameDtos, true);
            testPlotFetcher.fetchPlots(metricNodeSet);
        } else {
            //???testPlotFetcher.removePlots(metricNameDtos);
            //??? dummy to avoid methods signature collapse
            boolean dummy = true;
            testPlotFetcher.removePlots(metricNodeSet,dummy);
        }
    }
}
