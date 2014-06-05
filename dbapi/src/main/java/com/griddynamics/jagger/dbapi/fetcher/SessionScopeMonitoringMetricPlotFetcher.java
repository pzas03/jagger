package com.griddynamics.jagger.dbapi.fetcher;

import com.google.common.collect.*;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.PlotDatasetDto;
import com.griddynamics.jagger.dbapi.util.SessionScopeDataUtil;
import com.griddynamics.jagger.util.Pair;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 6/5/14
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionScopeMonitoringMetricPlotFetcher extends MonitoringMetricPlotFetcher {

    private SessionScopeDataUtil sessionScopeDataUtil;

    public void setSessionScopeDataUtil(SessionScopeDataUtil sessionScopeDataUtil) {
        this.sessionScopeDataUtil = sessionScopeDataUtil;
    }

    @Override
    protected Set<Pair<MetricNameDto, List<PlotDatasetDto>>> getResult(Collection<MetricRawData> allRawData, List<MetricNameDto> metricNames) {

        Multimap<String, MetricRawData> metricIdRawMap = sessionScopeDataUtil.getMetricIdRawMap(allRawData, allRawData.iterator().next().getSessionId());

        Multimap<MetricNameDto, PlotDatasetDto> metricNamePlotMap = ArrayListMultimap.create();
        for (MetricNameDto metricName : metricNames) {
            Collection<MetricRawData> rawDatas;

            if (metricIdRawMap.isEmpty())
                continue;
            rawDatas = metricIdRawMap.get(metricName.getMetricName());
            if (rawDatas == null || rawDatas.isEmpty()) {
                continue;
            }

            metricNamePlotMap.put(metricName, assemble(metricName, rawDatas));
        }


        Set<Pair<MetricNameDto, List<PlotDatasetDto>>> resultSet = new HashSet<Pair<MetricNameDto, List<PlotDatasetDto>>>(metricNames.size());

        for (MetricNameDto metricName : metricNamePlotMap.keySet()) {
            List<PlotDatasetDto> plotDatasetDtoList = new ArrayList<PlotDatasetDto>(metricNamePlotMap.get(metricName));
            resultSet.add(Pair.of(
                    metricName,
                    plotDatasetDtoList
            ));
        }

        return resultSet;
    }
}
