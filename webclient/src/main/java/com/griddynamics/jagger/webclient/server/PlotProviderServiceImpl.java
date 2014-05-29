package com.griddynamics.jagger.webclient.server;

import com.google.common.base.Joiner;
import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.csv.PlotToCsvGenerator;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.PlotSeriesDto;
import com.griddynamics.jagger.webclient.client.PlotProviderService;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import org.springframework.beans.factory.annotation.Required;

import java.io.*;
import java.util.*;


/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public class PlotProviderServiceImpl implements PlotProviderService {

    private DatabaseService databaseService;

    private NewFileStorage fileStorage;

    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Required
    public void setFileStorage(NewFileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public Map<MetricNode, PlotSeriesDto> getPlotData(Set<MetricNode> plots) throws RuntimeException {
        return databaseService.getPlotDataByMetricNode(plots);
    }

    @Override
    public String downloadInCsv(MetricNode metricNode)  {

        try {
            // getPlotData :
            Set<MetricNode> metricNodeSet = new HashSet<MetricNode>(1);
            metricNodeSet.add(metricNode);

            Map<MetricNode, PlotSeriesDto> plotsMap = getPlotData(metricNodeSet);
            PlotSeriesDto plot = plotsMap.get(metricNode);

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
