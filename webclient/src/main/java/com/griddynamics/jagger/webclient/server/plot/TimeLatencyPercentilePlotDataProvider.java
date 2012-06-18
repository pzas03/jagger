package com.griddynamics.jagger.webclient.server.plot;

import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.webclient.client.dto.PlotDatasetDto;
import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;
import com.griddynamics.jagger.webclient.client.dto.PointDto;
import com.griddynamics.jagger.webclient.server.ColorCodeGenerator;
import com.griddynamics.jagger.webclient.server.DataProcessingUtil;
import com.griddynamics.jagger.webclient.server.DefaultWorkloadParameters;
import com.griddynamics.jagger.webclient.server.LegendProvider;
import org.springframework.beans.factory.annotation.Required;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class TimeLatencyPercentilePlotDataProvider implements PlotDataProvider {
    private LegendProvider legendProvider;
    private EntityManager entityManager;

    @Required
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
                "select tis.time, ps.percentileKey, ps.percentileValue from TimeLatencyPercentile as ps inner join ps.timeInvocationStatistics as tis where tis.taskData.id=:taskId")
                .setParameter("taskId", taskId).getResultList();

        if (rawData == null) {
            return Collections.emptyList();
        }

        Map<String, List<PointDto>> percentiles = new HashMap<String, List<PointDto>>();
        double previousPercentileValue = 0.0;
        for (Object[] raw : rawData) {
            if (percentiles.get(raw[1].toString()) == null) {
                percentiles.put(raw[1].toString(), new ArrayList<PointDto>(rawData.size()));
            }
            List<PointDto> list = percentiles.get(raw[1].toString());

            double x = DataProcessingUtil.round((Long) raw[0] / 1000.0D);
            double y = DataProcessingUtil.round(((Double) raw[2] - previousPercentileValue) / 1000);
            list.add(new PointDto(x, y));

            previousPercentileValue = y;
        }
        Set<PlotDatasetDto> plotSeries = new HashSet<PlotDatasetDto>();
        for (Map.Entry<String, List<PointDto>> entry : percentiles.entrySet()) {
            String legend = DefaultWorkloadParameters.fromDescription(entry.getKey()).getDescription();
            plotSeries.add(new PlotDatasetDto(entry.getValue(), legend, ColorCodeGenerator.getHexColorCode()));
        }

        plotSeriesDto = new PlotSeriesDto(plotSeries, "Time, sec", "", legendProvider.getPlotHeader(taskId, plotName));

        return Collections.singletonList(plotSeriesDto);
    }

    //TODO Refactor it
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
                    "select tis.time, ps.percentileKey, ps.percentileValue from TimeLatencyPercentile as ps inner join ps.timeInvocationStatistics as tis where tis.taskData.id=:taskId")
                    .setParameter("taskId", taskId).getResultList();

            if (rawData == null) {
                continue;
            }

            Map<String, List<PointDto>> percentiles = new HashMap<String, List<PointDto>>();
            double previousPercentileValue = 0.0;
            for (Object[] raw : rawData) {
                if (percentiles.get(raw[1].toString()) == null) {
                    percentiles.put(raw[1].toString(), new ArrayList<PointDto>(rawData.size()));
                }
                List<PointDto> list = percentiles.get(raw[1].toString());

                double x = DataProcessingUtil.round((Long) raw[0] / 1000.0D);
                double y = DataProcessingUtil.round(((Double) raw[2] - previousPercentileValue) / 1000);
                list.add(new PointDto(x, y));

                previousPercentileValue = y;
            }

            for (Map.Entry<String, List<PointDto>> entry : percentiles.entrySet()) {
                String legend = "#" + taskData.getSessionId() + ": " + DefaultWorkloadParameters.fromDescription(entry.getKey()).getDescription();
                plotDatasetDtoList.add(new PlotDatasetDto(entry.getValue(), legend, ColorCodeGenerator.getHexColorCode()));
            }
        }

        return Collections.singletonList(new PlotSeriesDto(plotDatasetDtoList, "Time, sec", "", legendProvider.getPlotHeader(taskIds, plotName)));
    }
}
