package com.griddynamics.jagger.webclient.server;

import com.google.common.base.Joiner;
import com.griddynamics.jagger.dbapi.csv.PlotToCsvGenerator;
import com.griddynamics.jagger.dbapi.dto.*;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.model.MetricRankingProvider;
import com.griddynamics.jagger.dbapi.model.PlotNode;
import com.griddynamics.jagger.webclient.client.DownloadService;
import com.griddynamics.jagger.webclient.client.MetricDataService;
import com.griddynamics.jagger.webclient.client.PlotProviderService;
import org.springframework.beans.factory.annotation.Required;

import java.io.OutputStream;
import java.util.*;

public class DownloadServiceImpl implements DownloadService {

    private PlotProviderService plotProviderService;

    private MetricDataService metricDataService;

    private NewFileStorage fileStorage;

    @Required
    public void setPlotProviderService(PlotProviderService plotProviderService) {
        this.plotProviderService = plotProviderService;
    }

    @Required
    public void setMetricDataService(MetricDataService metricDataService) {
        this.metricDataService = metricDataService;
    }

    @Required
    public void setFileStorage(NewFileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public String createPlotCsvFile(MetricNode metricNode) throws RuntimeException {
        try {
            // getPlotData :
            Set<MetricNode> metricNodeSet = Collections.singleton(metricNode);
            PlotSeriesDto plot;

            if (metricNode instanceof PlotNode) {
                Map<MetricNode, PlotSeriesDto> plotsMap = plotProviderService.getPlotData(metricNodeSet);
                plot = plotsMap.get(metricNode);
            } else {
                Map<MetricNode, List<MetricDto>> map = metricDataService.getMetrics(metricNodeSet);

                List<MetricDto> metricDtos = map.get(metricNode);

                if (metricDtos == null || metricDtos.isEmpty()) {
                    throw new RuntimeException("No trend plot found for " + metricNode.getDisplayName());
                }

                List<PlotDatasetDto> plotDatasets = new ArrayList<PlotDatasetDto>(metricDtos.size());

                // sort trends plots
                MetricRankingProvider.sortMetrics(metricDtos);

                String plotHeader = metricDtos.get(0).getMetricName().getTest().getTaskName() + ", " +
                        metricNode.getDisplayName();

                for (MetricDto metricDto : metricDtos) {
                    plotDatasets.add(metricDto.getPlotDatasetDto());
                }

               plot = new PlotSeriesDto(plotDatasets, "Sessions", "", plotHeader);

            }

            if (plot == null) {
                throw new RuntimeException("could not find plot data for " + metricNode.toString());
            }

            String sessionsPrefix = getSessionPrefix(metricNode.getMetricNameDtoList());

            String fileKey = sessionsPrefix + ':' + plot.getPlotHeader();

            if (fileStorage.exists(fileKey)) {
                // return same object
                return fileKey;
            }

            OutputStream out = fileStorage.create(fileKey);

            // create csv file
            PlotToCsvGenerator.generateCsvFile(plot, out);

            return fileKey;
        } catch (Exception e) {
            throw new RuntimeException("errors while creating csv file", e);
        }
    }

    private String getSessionPrefix(List<MetricNameDto> metricNameDtoList) {

        Set<String> sessionIds = new HashSet<String>();

        for (MetricNameDto metricNameDto : metricNameDtoList) {
            sessionIds.addAll(metricNameDto.getTest().getSessionIds());
        }

        Joiner joiner = Joiner.on(", ");
        return joiner.join(sessionIds);
    }
}
