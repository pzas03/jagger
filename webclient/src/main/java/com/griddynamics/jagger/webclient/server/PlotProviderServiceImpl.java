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
import java.util.Collections;
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
                    "select tis.time, tis.throughput from TimeInvocationStatistics as tis where tis.taskData.id=:taskId").setParameter("taskId", taskId).getResultList();

            if (rawData == null) {
                return new PlotDatasetDto();
            }

            List<PointDto> pointDtoList = new ArrayList<PointDto>(rawData.size());
            for (Object[] raw : rawData) {
                double x = (Long) raw[0];
                double y = (Double) raw[1];
                pointDtoList.add(new PointDto(x/1000.0, y));
            }

            plotDatasetDto = new PlotDatasetDto(pointDtoList, "Throughput (tps/sec) for task-"+taskId, "Time, sec", "", "#007f00");

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
                    "select tis.time, tis.latency from TimeInvocationStatistics as tis where tis.taskData.id=:taskId").setParameter("taskId", taskId).getResultList();

            if (rawData == null) {
                return new PlotDatasetDto();
            }

            List<PointDto> pointDtoList = new ArrayList<PointDto>(rawData.size());
            for (Object[] raw : rawData) {
                double x = (Long) raw[0];
                double y = new BigDecimal((Double) raw[1]).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
                pointDtoList.add(new PointDto(x/1000.0, y));
            }

            plotDatasetDto = new PlotDatasetDto(pointDtoList, "Latency (sec/sec) for task-"+taskId, "Time, sec", "", "#007f00");

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
}
