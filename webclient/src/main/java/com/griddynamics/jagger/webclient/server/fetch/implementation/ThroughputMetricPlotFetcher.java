package com.griddynamics.jagger.webclient.server.fetch.implementation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.util.Pair;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.PlotDatasetDto;
import com.griddynamics.jagger.webclient.client.dto.PointDto;
import com.griddynamics.jagger.webclient.server.ColorCodeGenerator;
import com.griddynamics.jagger.webclient.server.DataProcessingUtil;
import com.griddynamics.jagger.webclient.server.DefaultWorkloadParameters;
import com.griddynamics.jagger.webclient.server.fetch.MetricNameUtil;
import com.griddynamics.jagger.webclient.server.fetch.PlotsDbMetricDataFetcher;

import java.util.*;


public class ThroughputMetricPlotFetcher extends PlotsDbMetricDataFetcher {

    // from reporting.conf.xml
    private static final String THROUGHPUT_METRIC_PLOT_ID = "Throughput";

    @Override
    protected Set<Pair<MetricNameDto, List<PlotDatasetDto>>> fetchData(List<MetricNameDto> metricNames) {

        if (metricNames.isEmpty()) {
            return Collections.emptySet();
        }


        Set<Pair<MetricNameDto, List<PlotDatasetDto>>> resultSet = new HashSet<Pair<MetricNameDto, List<PlotDatasetDto>>>(metricNames.size());
        Set<Long> taskIds = new HashSet<Long>();
        for (MetricNameDto metricNameDto : metricNames) {
            taskIds.addAll(metricNameDto.getTaskIds());
        }

        List<Object[]> rawAllData = findAllTimeInvocationStatisticsByTaskDataIds(taskIds);

        if (rawAllData.isEmpty()) {
            throw new RuntimeException("Could not find Throughput data for " + metricNames);
        }

        Multimap<Long, Object[]> taskIdRawMap = ArrayListMultimap.create();
        for (Object[] rawData : rawAllData) {
            Long taskDataId = (Long)rawData[2];
            taskIdRawMap.put(taskDataId, rawData);
        }

        Multimap<MetricNameDto, PlotDatasetDto> metricNamePlotMap = ArrayListMultimap.create();

        Map <Long, Map<String, MetricNameDto>> mappedMetricNames = MetricNameUtil.getMappedMetricDtos(metricNames);

        for (Long taskDataId : taskIdRawMap.keySet()) {

            MetricNameDto metricName;
            try {
                metricName = mappedMetricNames.get(taskDataId).get(THROUGHPUT_METRIC_PLOT_ID);
            } catch (NullPointerException e) {
                throw new RuntimeException("cant find metric name dto in MetricNameUtil.getMappedMetricDtos(metricNames) " + metricNames);
            }

            Collection<Object[]> rawData =  taskIdRawMap.get(taskDataId);
            if (rawData.isEmpty()) {
                throw new RuntimeException("no Throughput data found for TaskDataId : " + taskDataId);
            }
            metricNamePlotMap.put(metricName, assemble(rawData, true));
        }

        for (MetricNameDto metricName : metricNamePlotMap.keySet()) {
            List<PlotDatasetDto> plotDatasetDtoList = new ArrayList<PlotDatasetDto>(metricNamePlotMap.get(metricName));
            resultSet.add(
                    Pair.of(
                            metricName,
                            plotDatasetDtoList
                    ));
        }

        return resultSet;
    }

    /**
     * @param taskIds ids of TaskData table
     * @return Objects {time, throughput, TaskData.id, sessionId}
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> findAllTimeInvocationStatisticsByTaskDataIds(Set<Long> taskIds) {
        return entityManager.createQuery(
            "select tis.time, tis.throughput, tis.taskData.id, tis.taskData.sessionId from TimeInvocationStatistics as tis " +
                    "where tis.taskData.id in (:taskIds)")
            .setParameter("taskIds", taskIds)
            .getResultList();
    }

    /**
     * @param rawData Objects {time, throughput, TaskData.id, sessionId}
     * @return list of curves for concrete metricName
     */
    private PlotDatasetDto assemble(Collection<Object[]> rawData, boolean addSessionPrefix) {

        List<PointDto> pointDtoList = DataProcessingUtil.convertFromRawDataToPointDto(rawData, 0, 1);

        String sessionId = (String)rawData.iterator().next()[3]; // get sessionId
        String legend = legendProvider.generatePlotLegend(sessionId, DefaultWorkloadParameters.THROUGHPUT.getDescription(), addSessionPrefix);
        return new PlotDatasetDto(pointDtoList, legend, ColorCodeGenerator.getHexColorCode());
    }
}
