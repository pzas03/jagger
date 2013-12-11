package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.PlotNode;
import com.griddynamics.jagger.webclient.client.components.control.model.TestDetailsNode;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 12/2/13
 */
public class TestDetailsNodeHandler extends TreeAwareHandler<TestDetailsNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<TestDetailsNode> event) {

        Set<PlotNameDto> dtos = new HashSet<PlotNameDto>();
        for (PlotNode plotNode : event.getItem().getPlots()) {
            dtos.add(plotNode.getPlotName());
        }

        if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
            testPlotFetcher.fetchPlots(dtos, true);
        } else {
            testPlotFetcher.removePlots(dtos);
        }
    }
}

