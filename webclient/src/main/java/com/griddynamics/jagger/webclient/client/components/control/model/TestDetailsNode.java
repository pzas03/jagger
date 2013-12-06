package com.griddynamics.jagger.webclient.client.components.control.model;

import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */

public class TestDetailsNode extends SimpleNode {

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
    public List<? extends SimpleNode> getChildren() {
        ArrayList<SimpleNode> result = new ArrayList<SimpleNode>();
        result.addAll(plots);
        return result;
    }
}
