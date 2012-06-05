package com.griddynamics.jagger.webclient.server.plot;

import com.griddynamics.jagger.webclient.client.dto.PlotDatasetDto;
import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;
import com.griddynamics.jagger.webclient.client.dto.PointDto;
import com.griddynamics.jagger.webclient.server.*;

import javax.persistence.EntityManager;
import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class ThroughputPlotDataProvider implements PlotDataProvider {
    private final LegendProvider legendProvider = new LegendProvider();

    @Override
    public List<PlotSeriesDto> getPlotData(long taskId, String plotName) {
        EntityManager entityManager = EntityManagerProvider.getEntityManagerFactory().createEntityManager();

        PlotSeriesDto plotSeriesDto;
        try {
            List<Object[]> rawData = (List<Object[]>) entityManager.createQuery(
                    "select tis.time, tis.throughput from TimeInvocationStatistics as tis where tis.taskData.id=:taskId")
                    .setParameter("taskId", taskId).getResultList();

            if (rawData == null) {
                return Collections.emptyList();
            }

            List<PointDto> pointDtoList = DataProcessingUtil.convertFromRawDataToPointDto(rawData, 0, 1);

            String legend = DefaultWorkloadParameters.THROUGHPUT.getDescription();
            PlotDatasetDto plotDatasetDto = new PlotDatasetDto(pointDtoList, legend, ColorCodeGenerator.getHexColorCode());
            Set<PlotDatasetDto> plotSeries = new HashSet<PlotDatasetDto>();
            plotSeries.add(plotDatasetDto);
            plotSeriesDto = new PlotSeriesDto(plotSeries, "Time, sec", "", legendProvider.getPlotHeader(taskId, plotName));
        } finally {
            entityManager.close();
        }

        return Collections.singletonList(plotSeriesDto);
    }
}
