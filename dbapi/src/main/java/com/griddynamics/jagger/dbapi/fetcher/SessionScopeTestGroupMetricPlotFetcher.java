package com.griddynamics.jagger.dbapi.fetcher;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.PlotDatasetDto;
import com.griddynamics.jagger.dbapi.util.SessionScopeDataUtil;
import com.griddynamics.jagger.util.Pair;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 6/5/14
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionScopeTestGroupMetricPlotFetcher extends CustomTestGroupMetricPlotFetcher {

    private SessionScopeDataUtil sessionScopeDataUtil;

    @Required
    public void setSessionScopeDataUtil(SessionScopeDataUtil sessionScopeDataUtil) {
        this.sessionScopeDataUtil = sessionScopeDataUtil;
    }

    @Override
    protected Set<Pair<MetricNameDto, List<PlotDatasetDto>>> getResult(Collection<MetricRawData> allRawData, List<MetricNameDto> metricNames) {

        Set<Long> taskIds = new HashSet<Long>();
        for (MetricNameDto metricName : metricNames) {
            taskIds.addAll(metricName.getTaskIds());
        }

        Multimap<String, MetricRawData> metricIdRawMap = sessionScopeDataUtil.getMetricIdRawMap(allRawData, taskIds);

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
