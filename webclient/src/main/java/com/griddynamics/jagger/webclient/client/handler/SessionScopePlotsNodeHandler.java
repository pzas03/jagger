package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.SessionPlotNode;
import com.griddynamics.jagger.webclient.client.components.control.model.SessionScopePlotsNode;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */
public class SessionScopePlotsNodeHandler extends TreeAwareHandler<SessionScopePlotsNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<SessionScopePlotsNode> event) {
        Set<PlotNameDto> dtos = new HashSet<PlotNameDto>();
        for (SessionPlotNode node: event.getItem().getPlots()) {
            dtos.add(node.getPlotNameDto());
        }

        if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
            sessionScopePlotFetcher.fetchPlots(dtos, true);
        } else {
            sessionScopePlotFetcher.removePlots(dtos);
        }
    }
}
