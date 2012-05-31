package com.griddynamics.jagger.webclient.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.griddynamics.jagger.webclient.client.PlotProviderService;
import com.griddynamics.jagger.webclient.client.dto.PlotDatasetDto;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.PointDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public class PlotProviderServiceImpl extends RemoteServiceServlet implements PlotProviderService {
    private static final Logger log = LoggerFactory.getLogger(PlotProviderServiceImpl.class);

    @Override
    public List<PlotNameDto> getPlotListForTask(long taskId) {
        List<PlotNameDto> plotNameDtoList = new ArrayList<PlotNameDto>(Plot.values().length);
        for (Plot name : Plot.values()) {
            plotNameDtoList.add(new PlotNameDto(taskId, name.getText()));
        }
        return plotNameDtoList;
    }

    @Override
    public PlotDatasetDto getThroughputData(long taskId) {
        EntityManager entityManager = EntityManagerProvider.getEntityManagerFactory().createEntityManager();

        PlotDatasetDto plotDatasetDto;
        try {
            List<Object[]> rawData = (List<Object[]>) entityManager.createQuery(
                    "select tis.time, tis.throughput from TimeInvocationStatistics as tis where tis.taskData.id=:taskId")
                    .setParameter("taskId", taskId).getResultList();

            if (rawData == null) {
                return new PlotDatasetDto();
            }

            List<PointDto> pointDtoList = convertFromRawData(rawData);

            String legend = fetchLegend(entityManager, taskId, Plot.THROUGHPUT, "tps/sec");
            plotDatasetDto = new PlotDatasetDto(pointDtoList, legend, "Time, sec", "", ColorCodeGenerator.getHexColorCode());

            log.info("Throughput for taskId={} is: {}", taskId, pointDtoList);
        } finally {
            entityManager.close();
        }

        return plotDatasetDto;
    }

    @Override
    public PlotDatasetDto getLatencyData(long taskId) {
        EntityManager entityManager = EntityManagerProvider.getEntityManagerFactory().createEntityManager();

        PlotDatasetDto plotDatasetDto;
        try {
            List<Object[]> rawData = (List<Object[]>) entityManager.createQuery(
                    "select tis.time, tis.latency from TimeInvocationStatistics as tis where tis.taskData.id=:taskId")
                    .setParameter("taskId", taskId).getResultList();

            if (rawData == null) {
                return new PlotDatasetDto();
            }

            List<PointDto> pointDtoList = convertFromRawData(rawData);

            String legend = fetchLegend(entityManager, taskId, Plot.LATENCY, "sec/sec");
            plotDatasetDto = new PlotDatasetDto(pointDtoList, legend, "Time, sec", "", ColorCodeGenerator.getHexColorCode());

            log.info("Latency for taskId={} is: {}", taskId, pointDtoList);
        } finally {
            entityManager.close();
        }

        return plotDatasetDto;
    }

    @Override
    public PlotDatasetDto getPlotData(long taskId, String plotType) {
        Plot plot = Enum.valueOf(Plot.class, plotType.toUpperCase());

        if (plot == Plot.THROUGHPUT) {
            return getThroughputData(taskId);
        } else if (plot == Plot.LATENCY) {
            return getLatencyData(taskId);
        }

        throw new UnsupportedOperationException("Plot type " + plot + " doesn't supported");
    }

    private String fetchLegend(EntityManager entityManager, long taskId, Plot plot, String unitOfMeasurement) {
        Object[] legendData = (Object[]) entityManager.createQuery("select td.sessionId, td.taskName from TaskData as td where td.id=:taskId").
                setParameter("taskId", taskId).getSingleResult();

        return generatePlotLegend(legendData[0].toString(), legendData[1].toString(), plot, unitOfMeasurement);
    }

    private static List<PointDto> convertFromRawData(List<Object[]> rawData) {
        List<PointDto> pointDtoList = new ArrayList<PointDto>(rawData.size());
        for (Object[] raw : rawData) {
            double x = round((Long) raw[0] / 1000.0D);
            double y = round((Double) raw[1]);
            pointDtoList.add(new PointDto(x, y));
        }
        return pointDtoList;
    }

    private static double round(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    private static String generatePlotLegend(String sessionId, String taskName, Plot plot, String unitOfMeasurement) {
        StringBuilder builder = new StringBuilder();
        builder
                .append("Session ")
                .append(sessionId)
                .append(", ")
                .append(taskName)
                .append(", ")
                .append(plot.getText())
                .append(" (")
                .append(unitOfMeasurement)
                .append(")");

        return builder.toString();
    }
}
