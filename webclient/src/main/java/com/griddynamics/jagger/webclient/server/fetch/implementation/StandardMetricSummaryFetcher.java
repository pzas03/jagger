package com.griddynamics.jagger.webclient.server.fetch.implementation;

import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadTaskData;
import com.griddynamics.jagger.webclient.client.dto.MetricDto;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.MetricValueDto;
import com.griddynamics.jagger.webclient.server.fetch.SummaryDbMetricDataFetcher;

import java.util.*;

public class StandardMetricSummaryFetcher extends SummaryDbMetricDataFetcher {

    @Override
    protected Set<MetricDto> fetchData(List<MetricNameDto> standardMetricNames) {

        Set<MetricDto> resultSet = new HashSet<MetricDto>();

        resultSet.addAll(getRestMetrics(standardMetricNames));

        return resultSet;
    }

    private Collection<? extends MetricDto> getRestMetrics(List<MetricNameDto> restMetricNames) {

        if (restMetricNames.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        Set<Long> taskIds = new HashSet<Long>();
        for (MetricNameDto metricName : restMetricNames) {
            taskIds.addAll(metricName.getTaskIds());
        }

        List<WorkloadTaskData> workloadTaskDatas = (List<WorkloadTaskData>)entityManager.createNativeQuery("select * " +
                "        from WorkloadTaskData as wtd join TaskData as td on td.id in (:ids)" +
                "         and wtd.taskId=td.taskId and wtd.sessionId=td.sessionId", WorkloadTaskData.class)
                .setParameter("ids", taskIds)
                .getResultList();

        if (workloadTaskDatas.isEmpty()) {
            log.warn("Could not find data for {}", restMetricNames);
            return Collections.EMPTY_LIST;
        }

        Set<MetricDto> resultSet = new HashSet<MetricDto>();
        for (MetricNameDto metricName : restMetricNames) {
            MetricDto metricDto = new MetricDto();
            resultSet.add(metricDto);
            metricDto.setMetricName(metricName);
            metricDto.setValues(new HashSet<MetricValueDto>());
            for (WorkloadTaskData workloadTaskData : workloadTaskDatas) {
                if (metricName.getTest().getTaskName().equals(workloadTaskData.getScenario().getName())
                        && metricName.getTest().getSessionIds().contains(workloadTaskData.getSessionId())) {

                    MetricValueDto mvd = new MetricValueDto();
                    mvd.setSessionId(Long.parseLong(workloadTaskData.getSessionId()));
                    if (metricName.getMetricName().equals(StandardMetrics.AVG_LATENCY.getMetricName())) {
                        mvd.setValue(workloadTaskData.getAvgLatency().toString());
                    } else if (metricName.getMetricName().equals(StandardMetrics.SAMPLES.getMetricName())) {
                        mvd.setValue(workloadTaskData.getSamples().toString());
                    } else if (metricName.getMetricName().equals(StandardMetrics.SUCCESS_RATE.getMetricName())) {
                        mvd.setValue(workloadTaskData.getSuccessRate().toString());
                    } else if (metricName.getMetricName().equals(StandardMetrics.THROUGHPUT.getMetricName())) {
                        mvd.setValue(workloadTaskData.getThroughput().toString());
                    }
                    metricDto.getValues().add(mvd);
                }
            }
            metricDto.setPlotSeriesDtos(generatePlotSeriesDto(metricDto));
        }

        return resultSet;
    }

    public enum StandardMetrics {

        THROUGHPUT("throughput"),
        DURATION("duration"),
        AVG_LATENCY("avgLatency"),
        SUCCESS_RATE("successRate"),
        SAMPLES("samples");

        private String metricName;

        private StandardMetrics(String metricName) {
            this.metricName = metricName;
        }

        String getMetricName() {
            return metricName;
        }
    }
}
