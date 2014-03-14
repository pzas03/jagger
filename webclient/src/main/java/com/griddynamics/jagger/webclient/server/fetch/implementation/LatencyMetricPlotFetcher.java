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


public class LatencyMetricPlotFetcher extends PlotsDbMetricDataFetcher {

    // from reporting.conf.xml
    private static final String LATENCY_METRIC_PLOT_ID = "Latency";

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

        List<Object[]> rawAllData = findAllTimeInvocationStatisticsByTaskData(taskIds);

        if (rawAllData.isEmpty()) {
            throw new RuntimeException("Could not find Latency data for " + metricNames);
        }

        Multimap<Long, Object[]> taskIdRawMap = ArrayListMultimap.create();
        for (Object[] rawData : rawAllData) {
            Long taskDataId = (Long)rawData[3];
            taskIdRawMap.put(taskDataId, rawData);
        }

        Multimap<MetricNameDto, PlotDatasetDto> metricNamePlotMap = ArrayListMultimap.create();

        Map <Long, Map<String, MetricNameDto>> mappedMetricNames = MetricNameUtil.getMappedMetricDtos(metricNames);

        for (Long taskDataId : taskIdRawMap.keySet()) {

            MetricNameDto metricName;
            try {
                metricName = mappedMetricNames.get(taskDataId).get(LATENCY_METRIC_PLOT_ID);
            } catch (NullPointerException e) {
                throw new RuntimeException("cant find metric name dto in MetricNameUtil.getMappedMetricDtos(metricNames) " + metricNames);
            }

            Collection<Object[]> rawData =  taskIdRawMap.get(taskDataId);
            if (rawData.isEmpty()) {
                throw new RuntimeException("no Throughput data found for TaskDataId : " + taskDataId);
            }
            metricNamePlotMap.putAll(metricName, assemble(rawData));
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

    private List<PlotDatasetDto> assemble(Collection<Object[]> rawData) {
        List<PlotDatasetDto> plotDatasetDtoList = new ArrayList<PlotDatasetDto>(2);

        String sessionId = (String)rawData.iterator().next()[4]; // get sessionId
        List<PointDto> pointDtoList = DataProcessingUtil.convertFromRawDataToPointDto(rawData, 0, 1);
        String legend = legendProvider.generatePlotLegend(sessionId, DefaultWorkloadParameters.LATENCY.getDescription(), true);
        PlotDatasetDto plotDatasetDto = new PlotDatasetDto(pointDtoList, legend, ColorCodeGenerator.getHexColorCode());
        plotDatasetDtoList.add(plotDatasetDto);

        pointDtoList = DataProcessingUtil.convertFromRawDataToPointDto(rawData, 0, 2);
        legend = legendProvider.generatePlotLegend(sessionId, DefaultWorkloadParameters.LATENCY_STD_DEV.getDescription(), true);
        plotDatasetDto = new PlotDatasetDto(pointDtoList, legend, ColorCodeGenerator.getHexColorCode());
        plotDatasetDtoList.add(plotDatasetDto);

        return plotDatasetDtoList;
    }


    /**
`     * @param taskIds ids of TaskData table
     * @return list of Objects {time, latency, latencyStdDev, TaskData id, session id}
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> findAllTimeInvocationStatisticsByTaskData(Collection<Long> taskIds) {
        return entityManager.createQuery(
                "select tis.time, tis.latency, tis.latencyStdDev, tis.taskData.id, tis.taskData.sessionId " +
                        "from TimeInvocationStatistics as tis where tis.taskData.id in (:taskIds)")
                .setParameter("taskIds", taskIds)
                .getResultList();
    }
}
