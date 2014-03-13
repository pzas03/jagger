package com.griddynamics.jagger.webclient.client.dto;

import com.griddynamics.jagger.webclient.client.components.control.model.PlotNode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

// The only goal of this class to return 2 elements from server side to client in single call
// Object will contain:
// - names of monitoring parameters
// - names of monitoring agents
public class MonitoringSupportDto implements Serializable {
    private Map<TaskDataDto, List<PlotNode>> monitoringPlotNodes;
    private Map<String,Set<String>> monitoringAgentNames;

    public void MonitoringPlotDataProvider() {}

    public void init (Map<TaskDataDto, List<PlotNode>> monitoringPlotNodes, Map<String,Set<String>> monitoringAgentNames) {
        this.monitoringPlotNodes = monitoringPlotNodes;
        this.monitoringAgentNames = monitoringAgentNames;
    }

    public Map<String,Set<String>> getMonitoringAgentNames() {
        return monitoringAgentNames;
    }

    public Map<TaskDataDto, List<PlotNode>> getMonitoringPlotNodes() {
        return monitoringPlotNodes;
    }
}
