package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.TestNode;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;


/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */
public class TestNodeHandler extends TreeAwareHandler<TestNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<TestNode> event) {

        if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
            sessionComparisonPanel.addTestInfo(event.getItem().getTaskDataDto());
        } else {
            sessionComparisonPanel.removeTestInfo(event.getItem().getTaskDataDto());
        }
        metricFetcher.fetchMetrics(tree.getCheckedMetrics(), true);
    }
}
