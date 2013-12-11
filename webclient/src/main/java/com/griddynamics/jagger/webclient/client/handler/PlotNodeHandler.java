package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.PlotNode;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 12/2/13
 */
public class PlotNodeHandler extends TreeAwareHandler<PlotNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<PlotNode> event) {
        testPlotFetcher.fetchPlots(tree.getCheckedPlots(), true);
    }
}
