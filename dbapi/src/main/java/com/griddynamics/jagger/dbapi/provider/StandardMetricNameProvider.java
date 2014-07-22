package com.griddynamics.jagger.dbapi.provider;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;
import org.springframework.beans.factory.annotation.Required;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

public class StandardMetricNameProvider implements MetricNameProvider {

    private List<MetricNameDto> standardMetricNameDtoList;

    private final List<String> standardAsCustomMetricIds;

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    {
        standardAsCustomMetricIds = new ArrayList<String>();
        standardAsCustomMetricIds.add(StandardMetricsNamesUtil.THROUGHPUT_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX);
        standardAsCustomMetricIds.add(StandardMetricsNamesUtil.LATENCY_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX);
        standardAsCustomMetricIds.add(StandardMetricsNamesUtil.LATENCY_STD_DEV_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX);
        standardAsCustomMetricIds.add(StandardMetricsNamesUtil.ITERATION_SAMPLES_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX);
        standardAsCustomMetricIds.add(StandardMetricsNamesUtil.DURATION_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX);
        standardAsCustomMetricIds.add(StandardMetricsNamesUtil.FAIL_COUNT_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX);
        standardAsCustomMetricIds.add(StandardMetricsNamesUtil.SUCCESS_RATE_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX);
    }

    public List<String> getStandardAsCustomMetricIds() {
        return standardAsCustomMetricIds;
    }

    @Required
    public void setStandardMetricNameDtoList(List<MetricNameDto> standardMetricNameDtoList) {
        this.standardMetricNameDtoList = standardMetricNameDtoList;
    }

    @Override
    public Set<MetricNameDto> getMetricNames(List<TaskDataDto> tests) {

        Set<MetricNameDto> result = new HashSet<MetricNameDto>();

        for (TaskDataDto taskDataDto : tests) {
            for (MetricNameDto metricNameDto : standardMetricNameDtoList) {
                MetricNameDto metric = new MetricNameDto();
                metric.setMetricName(metricNameDto.getMetricName());
                metric.setMetricDisplayName(metricNameDto.getMetricDisplayName());
                metric.setOrigin(metricNameDto.getOrigin());
                metric.setTest(taskDataDto);
                metric.setMetricNameSynonyms(metricNameDto.getMetricNameSynonyms());
                result.add(metric);
            }
        }

        return result;
    }
}
