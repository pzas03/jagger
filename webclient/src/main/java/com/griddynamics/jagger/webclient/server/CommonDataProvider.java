package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */
public interface CommonDataProvider {

    Set<MetricNameDto> getMetricNames(Set<TaskDataDto> task);

    List<TaskDataDto> getTaskDataForSession(String sessionId);

    List<TaskDataDto> getTaskDataForSessions(Set<String> sessionIds);

    Set<PlotNameDto> getPlotNames(Set<String> sessionIds, TaskDataDto task);

    Set<PlotNameDto> getSessionScopePlotNames(String sessionId);
}
