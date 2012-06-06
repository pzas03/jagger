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
public class TimeLatencyPercentilePlotDataProvider implements PlotDataProvider {
    private LegendProvider legendProvider;

    public void setLegendProvider(LegendProvider legendProvider) {
        this.legendProvider = legendProvider;
    }

    @Override
    public List<PlotSeriesDto> getPlotData(long taskId, String plotName) {
        EntityManager entityManager = EntityManagerProvider.getEntityManagerFactory().createEntityManager();

        PlotSeriesDto plotSeriesDto;
        try {
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
        } finally {
            entityManager.close();
        }

        return Collections.singletonList(plotSeriesDto);
    }
}
