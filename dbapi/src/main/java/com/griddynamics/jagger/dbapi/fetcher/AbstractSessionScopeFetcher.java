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
 * Date: 6/10/14
 * Time: 4:07 PM
 *
 * This is class is needed because session scope processing of data for test group metrics equal
 * processing monitoring parameters. We use delegates required classes.
 *
 */

public class AbstractSessionScopeFetcher<F extends AbstractMetricPlotFetcher> extends PlotsDbMetricDataFetcher{

    private F abstractMetricPlotFetcher;
    private SessionScopeDataUtil sessionScopeDataUtil;

    @Required
    public void setSessionScopeDataUtil(SessionScopeDataUtil sessionScopeDataUtil) {
        this.sessionScopeDataUtil = sessionScopeDataUtil;
    }

    @Required
    public void setAbstractMetricPlotFetcher(F abstractMetricPlotFetcher){
        this.abstractMetricPlotFetcher = abstractMetricPlotFetcher;
    }

    @Override
    protected Set<Pair<MetricNameDto, List<PlotDatasetDto>>> fetchData(List<MetricNameDto> metricNames) {
        if (metricNames.isEmpty()) {
            return Collections.emptySet();
        }

        Collection<AbstractMetricPlotFetcher.MetricRawData> allRawData = abstractMetricPlotFetcher.getAllRawData(metricNames);

        if (allRawData.isEmpty()) {
            log.warn("No plot data found for metrics : {}", metricNames);
            return Collections.emptySet();
        }
        return getResult(allRawData, metricNames);
    }


    protected Set<Pair<MetricNameDto, List<PlotDatasetDto>>> getResult(Collection<AbstractMetricPlotFetcher.MetricRawData> allRawData, List<MetricNameDto> metricNames) {

        Set<Long> taskIds = new HashSet<Long>();
        for (MetricNameDto metricName : metricNames) {
            taskIds.addAll(metricName.getTaskIds());
        }

        Multimap<String, AbstractMetricPlotFetcher.MetricRawData> metricIdRawMap = sessionScopeDataUtil.getMetricIdRawMap(allRawData, taskIds);

        Multimap<MetricNameDto, PlotDatasetDto> metricNamePlotMap = ArrayListMultimap.create();
        for (MetricNameDto metricName : metricNames) {
            Collection<AbstractMetricPlotFetcher.MetricRawData> rawDatas;

            if (metricIdRawMap.isEmpty())
                continue;
            rawDatas = metricIdRawMap.get(metricName.getMetricName());
            if (rawDatas == null || rawDatas.isEmpty()) {
                continue;
            }

            metricNamePlotMap.put(metricName, abstractMetricPlotFetcher.assemble(metricName, rawDatas));
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
