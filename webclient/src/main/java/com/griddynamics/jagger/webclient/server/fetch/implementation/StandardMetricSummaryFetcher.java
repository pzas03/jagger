package com.griddynamics.jagger.webclient.server.fetch.implementation;

import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadTaskData;
import com.griddynamics.jagger.util.TimeUtils;
import com.griddynamics.jagger.webclient.client.dto.MetricDto;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.MetricValueDto;
import com.griddynamics.jagger.webclient.server.fetch.MetricNameUtil;
import com.griddynamics.jagger.webclient.server.fetch.SummaryDbMetricDataFetcher;

import java.math.BigInteger;
import java.util.*;

public class StandardMetricSummaryFetcher extends SummaryDbMetricDataFetcher {

    @Override
    protected Set<MetricDto> fetchData(List<MetricNameDto> standardMetricNames) {

        List<MetricNameDto> durationMetricNames = new ArrayList<MetricNameDto>();
        List<MetricNameDto> restMetricNames = new ArrayList<MetricNameDto>();

        for (MetricNameDto metricName : metricNames) {
            if (StandardMetrics.DURATION.getMetricName().equals(metricName.getMetricName())) {
                durationMetricNames.add(metricName);
            } else {
                restMetricNames.add(metricName);
            }
        }

        Set<MetricDto> resultSet = new HashSet<MetricDto>();

        resultSet.addAll(getDurationMetrics(durationMetricNames));

        resultSet.addAll(getRestMetrics(restMetricNames));

        return resultSet;
    }

    private Collection<? extends MetricDto> getRestMetrics(List<MetricNameDto> restMetricNames) {

        if (restMetricNames.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<Long> taskIds = new ArrayList<Long>();
        for (MetricNameDto metricName : restMetricNames) {
            taskIds.addAll(metricName.getTaskIds());
        }

        List<WorkloadTaskData> workloadTaskDatas = (List<WorkloadTaskData>)entityManager.createNativeQuery("select * "+
                "from WorkloadTaskData as workload where (workload.taskId, workload.sessionId) in " +
                "(select taskId, sessionId from TaskData where id in (:ids))", WorkloadTaskData.class)
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

    private Collection<? extends MetricDto> getDurationMetrics(List<MetricNameDto> durationMetricNames) {

        if (durationMetricNames.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        List<Long> taskIds = new ArrayList<Long>();

        for (MetricNameDto metricName : durationMetricNames) {
            taskIds.addAll(metricName.getTaskIds());
        }

        List<Object[]> result = entityManager.createNativeQuery("select workload.sessionId, workload.endTime, workload.startTime, taskData.id " +
                "from WorkloadData as workload inner join (select taskId, sessionId, id from TaskData where id in (:ids)) as taskData "+
                "on workload.taskId=taskData.taskId and workload.sessionId=taskData.sessionId ")
                .setParameter("ids", taskIds)
                .getResultList();


        if (result.isEmpty()) {
            log.warn("Could not find data for {}", durationMetricNames);
            return Collections.EMPTY_LIST;
        }

        return processDurationDataFromDatabase(result, durationMetricNames);
    }

    private Set<MetricDto> processDurationDataFromDatabase(List<Object[]> rawData, List<MetricNameDto> durationMetricNames) {

        Map<Long, Map<String, MetricDto>> mappedMetricDtos = MetricNameUtil.getMappedMetricDtos(durationMetricNames);

        Set<MetricDto> resultSet = new HashSet<MetricDto>();

        for (Object[] entry : rawData) {
            BigInteger taskId = (BigInteger) entry[3];
            Map<String, MetricDto> metricIdMap = mappedMetricDtos.get(taskId.longValue());
            if (metricIdMap == null) {
                throw new IllegalArgumentException("unknown task id in mapped metrics : " + taskId.longValue());
            }
            MetricDto metricDto = metricIdMap.get(StandardMetrics.DURATION.getMetricName());
            if (metricDto == null) {
                throw new IllegalArgumentException("could not find appropriate MetricDto : " + taskId.longValue());
            }
            resultSet.add(metricDto);

            MetricValueDto value = new MetricValueDto();
            Date[] date = new Date[2];
            date[0] = (Date)entry [1];
            date[1] = (Date)entry [2];
            value.setValueRepresentation(TimeUtils.formatDuration(date[0].getTime() - date[1].getTime()));
            value.setValue(String.valueOf( (date[0].getTime() - date[1].getTime()) / 1000));
            value.setSessionId(Long.parseLong(String.valueOf(entry[0])));
            metricDto.getValues().add(value);
        }

        for (MetricDto md : resultSet) {
            md.setPlotSeriesDtos(generatePlotSeriesDto(md));
        }

        return resultSet;
    }

    private enum StandardMetrics {

        THROUGHPUT("throughput"),
        DURATION("Duration"),
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
