package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.*;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */
public class DetailsNodeHandler extends TreeAwareHandler<DetailsNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<DetailsNode> event) {
        testPlotFetcher.fetchPlots(tree.getCheckedPlots(), true);
    }
}
