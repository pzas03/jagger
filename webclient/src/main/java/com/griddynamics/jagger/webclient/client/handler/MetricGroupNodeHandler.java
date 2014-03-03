package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.MetricGroupNode;
import com.griddynamics.jagger.webclient.client.components.control.model.MetricNode;
import com.griddynamics.jagger.webclient.client.components.control.model.PlotNode;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.LinkedHashSet;
import java.util.Set;


public class MetricGroupNodeHandler extends TreeAwareHandler<MetricGroupNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<MetricGroupNode> event) {

        int size = event.getItem().getMetrics().size();

        if (size > 0) {

            // todo
            // Same not nice approach as in CheckHandlerMap
            // Point to change in future
            // MetricGroupNode should contain appropriate fetcher and just call it here

            if (event.getItem().getMetrics().get(0).getClass() == MetricNode.class) {
                metricFetcher.fetchMetrics(tree.getCheckedMetrics(), true);
            }

            if (event.getItem().getMetrics().get(0).getClass() == PlotNode.class) {
                MetricGroupNode<PlotNode> testNode = event.getItem();

                //??? temp here
                Set<MetricNameDto> dtos = new LinkedHashSet<MetricNameDto>();

                Set<PlotNode> nodes = new LinkedHashSet<PlotNode>();
                for (PlotNode plotNode : testNode.getMetrics()) {
                    nodes.add(plotNode);
                    //??? temp here
                    dtos.addAll(plotNode.getMetricNameDtoList());
                }

                if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
                    //??? old way
                    testPlotFetcher.fetchPlots(dtos, true);


                } else {
                    // ??? temp here. should be by nodes
                    testPlotFetcher.removePlots(dtos);
                }
            }

        }
    }
}
