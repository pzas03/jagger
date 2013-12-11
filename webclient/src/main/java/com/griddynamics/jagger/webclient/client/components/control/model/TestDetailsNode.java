package com.griddynamics.jagger.webclient.client.components.control.model;

import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Corresponds to test as child of DetailsNode
 * User: amikryukov
 * Date: 11/27/13
 */

public class TestDetailsNode extends AbstractIdentifyNode {

    private TaskDataDto taskDataDto;

    private List<PlotNode> plots;

    public TaskDataDto getTaskDataDto() {
        return taskDataDto;
    }

    public void setTaskDataDto(TaskDataDto taskDataDto) {
        this.taskDataDto = taskDataDto;
    }

    public List<PlotNode> getPlots() {
        return plots;
    }

    public void setPlots(List<PlotNode> plots) {
        this.plots = plots;
    }

    @Override
    public String getDisplayName() {
        return taskDataDto.getTaskName();
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        ArrayList<AbstractIdentifyNode> result = new ArrayList<AbstractIdentifyNode>();
        result.addAll(plots);
        return result;
    }
}
