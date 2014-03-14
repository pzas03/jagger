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

public class TimeLatencyPercentileMetricPlotFetcher extends PlotsDbMetricDataFetcher {

    // from reporting.conf.xml
    private static final String TIME_LATENCY_PERCENTILE_METRIC_PLOT_ID = "Time Latency Percentile";

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
            throw new RuntimeException("Could not find Throughput data for " + metricNames);
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
                metricName = mappedMetricNames.get(taskDataId).get(TIME_LATENCY_PERCENTILE_METRIC_PLOT_ID);
            } catch (NullPointerException e) {
                throw new RuntimeException("cant find metric name dto in MetricNameUtil.getMappedMetricDtos(metricNames) " + metricNames);
            }

            Collection<Object[]> rawData =  taskIdRawMap.get(taskDataId);
            if (rawData.isEmpty()) {
                throw new RuntimeException("no Throughput data found for TaskDataId : " + taskDataId);
            }
            metricNamePlotMap.putAll(metricName, assemble(rawData, true));
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
     * @param taskIds  collection of TaskData table`s ids
     * @return list of Objects {time, percentileKey, percentileValue, TaskData id, sessionId}
     */
    @SuppressWarnings("unchecked")
    private List<Object[]> findAllTimeInvocationStatisticsByTaskData(Collection<Long> taskIds) {
        return entityManager.createQuery(
                "select tis.time, ps.percentileKey, ps.percentileValue, tis.taskData.id, tis.taskData.sessionId from TimeLatencyPercentile as ps " +
                        "inner join ps.timeInvocationStatistics as tis where tis.taskData.id in (:taskIds)")
                .setParameter("taskIds", taskIds).getResultList();
    }

    private List<PlotDatasetDto> assemble(Collection<Object[]> rawData, boolean addSessionPrefix) {
        List<PlotDatasetDto> plotDatasetDtoList = new ArrayList<PlotDatasetDto>();
        Map<String, List<PointDto>> percentiles = new TreeMap<String, List<PointDto>>();
        String sessionId = (String)rawData.iterator().next()[4];
        double previousPercentileValue = 0.0;
        for (Object[] raw : rawData) {
            if (percentiles.get(raw[1].toString()) == null) {
                percentiles.put(raw[1].toString(), new ArrayList<PointDto>(rawData.size()));
            }
            List<PointDto> list = percentiles.get(raw[1].toString());

            double x = DataProcessingUtil.round((Long) raw[0] / 1000.0D);
            double y = DataProcessingUtil.round(((Double) raw[2] - previousPercentileValue) / 1000);
            list.add(new PointDto(x, y));

            previousPercentileValue = y;
        }

        for (Map.Entry<String, List<PointDto>> entry : percentiles.entrySet()) {
            DefaultWorkloadParameters parameter = DefaultWorkloadParameters.fromDescription(entry.getKey());
            String description = (parameter == null ? entry.getKey() : parameter.getDescription());
            String legend = legendProvider.generatePlotLegend(sessionId, description, addSessionPrefix);
            plotDatasetDtoList.add(new PlotDatasetDto(entry.getValue(), legend, ColorCodeGenerator.getHexColorCode()));
        }

        return plotDatasetDtoList;
    }
}
