package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.dto.PlotDatasetDto;
import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;
import com.griddynamics.jagger.webclient.client.dto.PointDto;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class LatencyPlotDataProvider implements PlotDataProvider {
    private final LegendProvider legendProvider = new LegendProvider();

    @Override
    public PlotSeriesDto getPlotData(long taskId) {
        EntityManager entityManager = EntityManagerProvider.getEntityManagerFactory().createEntityManager();

        PlotSeriesDto plotSeriesDto;
        try {
            List<Object[]> rawData = (List<Object[]>) entityManager.createQuery(
                    "select tis.time, tis.latency, tis.latencyStdDev from TimeInvocationStatistics as tis where tis.taskData.id=:taskId")
                    .setParameter("taskId", taskId).getResultList();

            if (rawData == null) {
                return new PlotSeriesDto();
            }

            List<PointDto> pointDtoList = DataProcessingUtil.convertFromRawDataToPointDto(rawData, 0, 1);

            String legend = legendProvider.getPlotLegend(taskId, Plot.LATENCY, "sec/sec");
            PlotDatasetDto plotDatasetDto = new PlotDatasetDto(pointDtoList, legend, ColorCodeGenerator.getHexColorCode());
            Set<PlotDatasetDto> plotSeries = new HashSet<PlotDatasetDto>();
            plotSeries.add(plotDatasetDto);

            pointDtoList = DataProcessingUtil.convertFromRawDataToPointDto(rawData, 0, 2);

            legend = legendProvider.getPlotLegend(taskId, Plot.LATENCY_STD_DEV, "sec/sec");
            plotDatasetDto = new PlotDatasetDto(pointDtoList, legend, ColorCodeGenerator.getHexColorCode());
            plotSeries.add(plotDatasetDto);


            plotSeriesDto = new PlotSeriesDto(plotSeries, "Time, sec", "");
        } finally {
            entityManager.close();
        }

        return plotSeriesDto;
    }
}
