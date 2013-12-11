package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.TestDetailsNode;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 12/2/13
 */
public class TestDetailsNodeHandler extends TreeAwareHandler<TestDetailsNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<TestDetailsNode> event) {

        testPlotFetcher.fetchPlots(tree.getCheckedPlots(), true);
    }
}

