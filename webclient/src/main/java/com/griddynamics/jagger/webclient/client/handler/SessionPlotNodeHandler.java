package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.SessionPlotNode;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 12/4/13
 */
public class SessionPlotNodeHandler extends TreeAwareHandler<SessionPlotNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<SessionPlotNode> event) {
        sessionScopePlotFetcher.fetchPlots(tree.getCheckedSessionScopePlots(), true);
    }
}
