package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.SessionScopePlotsNode;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */
public class SessionScopePlotsNodeHandler extends TreeAwareHandler<SessionScopePlotsNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<SessionScopePlotsNode> event) {
        sessionScopePlotFetcher.fetchPlots(tree.getCheckedSessionScopePlots(), true);
    }
}
