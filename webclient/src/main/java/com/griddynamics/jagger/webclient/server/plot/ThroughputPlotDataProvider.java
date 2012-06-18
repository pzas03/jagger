package com.griddynamics.jagger.webclient.server.plot;

import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.webclient.client.dto.PlotDatasetDto;
import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;
import com.griddynamics.jagger.webclient.client.dto.PointDto;
import com.griddynamics.jagger.webclient.server.ColorCodeGenerator;
import com.griddynamics.jagger.webclient.server.DataProcessingUtil;
import com.griddynamics.jagger.webclient.server.DefaultWorkloadParameters;
import com.griddynamics.jagger.webclient.server.LegendProvider;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class ThroughputPlotDataProvider implements PlotDataProvider {
    private LegendProvider legendProvider;
    private EntityManager entityManager;

    public void setLegendProvider(LegendProvider legendProvider) {
        this.legendProvider = legendProvider;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<PlotSeriesDto> getPlotData(long taskId, String plotName) {
        PlotSeriesDto plotSeriesDto;
        @SuppressWarnings("unchecked")
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

        return Collections.singletonList(plotSeriesDto);
    }

    // TODO Refactor it
    @Override
    public List<PlotSeriesDto> getPlotData(Set<Long> taskIds, String plotName) {
        checkNotNull(taskIds, "taskIds is null");
        checkArgument(!taskIds.isEmpty(), "taskIds is empty");
        checkNotNull(plotName, "plotName is null");

        List<PlotDatasetDto> plotDatasetDtoList = new ArrayList<PlotDatasetDto>(taskIds.size());
        for (long taskId : taskIds) {
            TaskData taskData = entityManager.find(TaskData.class, taskId);

            @SuppressWarnings("unchecked")
            List<Object[]> rawData = (List<Object[]>) entityManager.createQuery(
                    "select tis.time, tis.throughput from TimeInvocationStatistics as tis where tis.taskData.id=:taskId")
                    .setParameter("taskId", taskId).getResultList();

            if (rawData == null) {
                continue;
            }

            List<PointDto> pointDtoList = DataProcessingUtil.convertFromRawDataToPointDto(rawData, 0, 1);

            String legend = "#" + taskData.getSessionId() + ": " + DefaultWorkloadParameters.THROUGHPUT.getDescription();
            PlotDatasetDto plotDatasetDto = new PlotDatasetDto(pointDtoList, legend, ColorCodeGenerator.getHexColorCode());
            plotDatasetDtoList.add(plotDatasetDto);
        }

        return Collections.singletonList(new PlotSeriesDto(plotDatasetDtoList, "Time, sec", "", legendProvider.getPlotHeader(taskIds, plotName)));
    }
}
