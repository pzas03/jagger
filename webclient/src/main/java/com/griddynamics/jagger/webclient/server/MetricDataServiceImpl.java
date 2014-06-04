package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.dto.*;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.model.MetricRankingProvider;
import com.griddynamics.jagger.webclient.client.MetricDataService;
import com.griddynamics.jagger.webclient.client.dto.SummaryMetricDto;
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
    public Map<MetricNode, SummaryMetricDto> getMetrics(List<MetricNode> metricNodes) throws RuntimeException {

        List<MetricNameDto> metricNameDtos = new ArrayList<MetricNameDto>();
        for (MetricNode metricNode : metricNodes) {
            metricNameDtos.addAll(metricNode.getMetricNameDtoList());
        }
        List<MetricDto> allMetricDto = databaseService.getSummaryByMetricNameDto(metricNameDtos);

        Map<MetricNode, SummaryMetricDto> resultMap = new HashMap<MetricNode, SummaryMetricDto>();
        for (MetricDto metricDto : allMetricDto) {
            for (MetricNode metricNode : metricNodes) {
                if (metricNode.getMetricNameDtoList().contains(metricDto.getMetricName())) {
                    if (!resultMap.containsKey(metricNode)) {
                        resultMap.put(metricNode, new SummaryMetricDto());
                    }
                    resultMap.get(metricNode).getMetricDtoList().add(metricDto);
                    break;
                }
            }
        }

        // generate plotSeriesDto
        for (MetricNode metricNode : resultMap.keySet()) {
            SummaryMetricDto current = resultMap.get(metricNode);

            PlotSeriesDto plotSeriesDto = generatePlotSeriesDto(metricNode.getDisplayName(), current.getMetricDtoList());
            current.setPlotSeriesDto(plotSeriesDto);
        }


        return resultMap;
    }

    private PlotSeriesDto generatePlotSeriesDto(String displayName, List<MetricDto> metricDtos) {

        List<PlotDatasetDto> plotDatasets = new ArrayList<PlotDatasetDto>(metricDtos.size());

        // sort trends plots
        MetricRankingProvider.sortMetrics(metricDtos);

        String plotHeader = metricDtos.get(0).getMetricName().getTest().getTaskName() + ", " +
                displayName;

        for (MetricDto metricDto : metricDtos) {
            PlotDatasetDto plotDataSet = metricDto.getPlotDatasetDto();
            plotDatasets.add(plotDataSet);
        }

        return new PlotSeriesDto(plotDatasets, "Sessions", "", plotHeader);
    }
}