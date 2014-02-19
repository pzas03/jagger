package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.MetricGroupNode;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;


public class MetricGroupNodeHandler extends TreeAwareHandler<MetricGroupNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<MetricGroupNode> event) {
        metricFetcher.fetchMetrics(tree.getCheckedMetrics(), true);
    }
}
