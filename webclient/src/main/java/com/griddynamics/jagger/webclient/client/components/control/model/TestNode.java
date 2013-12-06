package com.griddynamics.jagger.webclient.client.components.control.model;

import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/26/13
 */
public class TestNode extends SimpleNode {

    private TaskDataDto taskDataDto;

    private List<MetricNode> metrics;
    private TestInfoNode testInfo;

    @Override
    public String toString() {
        return "TestNode class to string";
    }

    public TaskDataDto getTaskDataDto() {
        return taskDataDto;
    }

    public void setTaskDataDto(TaskDataDto taskDataDto) {
        this.taskDataDto = taskDataDto;
    }

    public List<MetricNode> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<MetricNode> metrics) {
        this.metrics = metrics;
    }

    public TestInfoNode getTestInfo() {
        return testInfo;
    }

    public void setTestInfo(TestInfoNode testInfo) {
        this.testInfo = testInfo;
    }

    @Override
    public String getDisplayName() {
        return taskDataDto.getTaskName();
    }

    @Override
    public List<? extends SimpleNode> getChildren() {
        ArrayList<SimpleNode> result = new ArrayList<SimpleNode>();
        if (testInfo != null) result.add(testInfo);
        result.addAll(metrics);
        return result;
    }
}
