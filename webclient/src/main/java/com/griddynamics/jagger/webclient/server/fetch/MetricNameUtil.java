package com.griddynamics.jagger.webclient.server.fetch;

import com.griddynamics.jagger.webclient.client.dto.MetricDto;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.MetricValueDto;

import java.util.*;

public class MetricNameUtil {

    /**
     * Map Collection of MetricNameDto first of all by TaskDataDto id, after all by metric name (metric id)
     * @param metricNameDtos collection of MetricNameDto to be mapped
     * @return Map <TaskDataDto.id, Map<metricName, MetricDto>>
     */
    public static Map<Long, Map<String, MetricDto>> getMappedMetricDtos(Collection<MetricNameDto> metricNameDtos) {
        Map<Long, Map<String, MetricDto>> taskIdMap = new HashMap<Long, Map<String, MetricDto>>();
        for (MetricNameDto metricName : metricNameDtos) {
            Set<Long> ids = metricName.getTaskIds();
            for (Long id : ids) {
                if (taskIdMap.get(id) == null) {
                    taskIdMap.put(id, new HashMap<String, MetricDto>());
                }
                Map<String, MetricDto> metricIdMap = taskIdMap.get(id);
                if (metricIdMap.get(metricName.getMetricName()) == null) {
                    MetricDto metricDto = new MetricDto();
                    metricDto.setMetricName(metricName);
                    metricDto.setValues(new HashSet<MetricValueDto>());
                    metricIdMap.put(metricName.getMetricName(), metricDto);
                } else {
                    throw new IllegalStateException(metricName.toString() + " already in Map");
                }
            }
        }
        return taskIdMap;
    }

    public static String getLatencyMetricName(double latencyKey) {
        return "Latency " + latencyKey + " %";
    }
}
