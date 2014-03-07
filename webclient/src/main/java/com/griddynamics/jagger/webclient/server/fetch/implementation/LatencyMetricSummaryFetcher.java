package com.griddynamics.jagger.webclient.server.fetch.implementation;

import com.griddynamics.jagger.webclient.client.dto.MetricDto;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.MetricValueDto;
import com.griddynamics.jagger.webclient.server.fetch.MetricNameUtil;
import com.griddynamics.jagger.webclient.server.fetch.SummaryDbMetricDataFetcher;

import java.util.*;

public class LatencyMetricSummaryFetcher extends SummaryDbMetricDataFetcher {

    @Override
    protected Set<MetricDto> fetchData(List<MetricNameDto> metricNames) {

        if (metricNames.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Set<Long> taskIds = new HashSet<Long>();
        Set<Double> percentileKeys = new HashSet<Double>();
        for (MetricNameDto metricName : metricNames) {
            taskIds.addAll(metricName.getTaskIds());
            percentileKeys.add(Double.parseDouble(metricName.getMetricName().split(" ")[1]));
        }
        //it is a latency metric
        List<Object[]> latency = entityManager.createQuery("select s.percentileValue, s.workloadProcessDescriptiveStatistics.taskData.id, s.workloadProcessDescriptiveStatistics.taskData.sessionId, s.percentileKey " +
                "from  WorkloadProcessLatencyPercentile as s " +
                "where s.workloadProcessDescriptiveStatistics.taskData.id in (:taskIds) " +
                "and s.percentileKey in (:latencyKey) ")
                .setParameter("taskIds", taskIds)
                .setParameter("latencyKey", percentileKeys)
                .getResultList();

        if (latency.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Map<Long, Map<String, MetricDto>> mappedMetricNames = MetricNameUtil.getMappedMetricDtos(metricNames);

        Set<MetricDto> resultSet = new HashSet<MetricDto>();

        for (Object[] temp : latency){

            Long taskId = (Long)temp[1];

            Map<String, MetricDto> metricIdMap = mappedMetricNames.get(taskId);
            if (metricIdMap == null) {
                throw new IllegalArgumentException("unknown task id in mapped metrics : " + taskId);
            }
            String metricId = MetricNameUtil.getLatencyMetricName((Long)temp[3]);
            MetricDto metricDto = metricIdMap.get(metricId);
            if (metricDto == null) {
                throw new IllegalArgumentException("could not find appropriate MetricDto : " + taskId);
            }

            resultSet.add(metricDto);

            MetricValueDto value = new MetricValueDto();
            value.setValue(String.format("%.3f", (Double)temp[0] / 1000));
            value.setTestId(Long.parseLong(temp[1].toString()));
            value.setSessionId(Long.parseLong(temp[2].toString()));
            metricDto.getValues().add(value);
        }

        for (MetricDto md : resultSet) {
            md.setPlotSeriesDtos(generatePlotSeriesDto(md));
        }

        return resultSet;
    }
}
