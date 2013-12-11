package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.MetricNode;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */
public class MetricNodeHandler extends TreeAwareHandler<MetricNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<MetricNode> event) {

        metricFetcher.fetchMetrics(tree.getCheckedMetrics(), true);
    }
}
