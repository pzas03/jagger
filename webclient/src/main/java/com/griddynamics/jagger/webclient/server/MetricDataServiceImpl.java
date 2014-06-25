package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.dto.MetricDto;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.webclient.client.MetricDataService;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 08.04.13
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */
public class MetricDataServiceImpl implements MetricDataService {

    private DatabaseService databaseService;

    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public Map<MetricNode, List<MetricDto>> getMetrics(Set<MetricNode> metricNodes, boolean isEnableDecisionsPerMetricFetching) throws RuntimeException {

        List<MetricNameDto> metricNameDtos = new ArrayList<MetricNameDto>();
        for (MetricNode metricNode : metricNodes) {
            metricNameDtos.addAll(metricNode.getMetricNameDtoList());
        }
        List<MetricDto> allMetricDto = databaseService.getSummaryByMetricNameDto(metricNameDtos, isEnableDecisionsPerMetricFetching);

        Map<MetricNode, List<MetricDto>> resultMap = new HashMap<MetricNode, List<MetricDto>>();
        for (MetricDto metricDto : allMetricDto) {
            for (MetricNode metricNode : metricNodes) {
                if (metricNode.getMetricNameDtoList().contains(metricDto.getMetricName())) {
                    if (!resultMap.containsKey(metricNode)) {
                        resultMap.put(metricNode, new ArrayList<MetricDto>());
                    }
                    resultMap.get(metricNode).add(metricDto);
                    break;
                }
            }
        }

        return resultMap;
    }
}