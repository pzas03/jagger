package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.SessionPlotNode;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 12/4/13
 */
public class SessionPlotNodeHandler extends TreeAwareHandler<SessionPlotNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<SessionPlotNode> event) {
        PlotNameDto plotName = event.getItem().getPlotNameDto();
        if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
            sessionScopePlotFetcher.fetchPlot(plotName, true);
        } else {
            sessionScopePlotFetcher.removePlot(plotName);
        }
    }
}
