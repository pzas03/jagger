package com.griddynamics.jagger.webclient.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.griddynamics.jagger.webclient.client.PlotProviderService;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.PointDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
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
    public List<PointDto> getThroughputData(long taskId) {
        EntityManager entityManager = EntityManagerProvider.getEntityManagerFactory().createEntityManager();

        List<PointDto> pointDtoList;
        try {
            List<Object[]> rawData = (List<Object[]>) entityManager.createQuery(
                    "select tis.time, tis.throughput from TimeInvocationStatistics as tis where tis.taskData.id=:taskId").setParameter("taskId", taskId).getResultList();

            if (rawData == null) {
                return Collections.emptyList();
            }

            pointDtoList = new ArrayList<PointDto>(rawData.size());
            for (Object[] raw : rawData) {
                double x = (Long) raw[0];
                double y = (Double) raw[1];
                pointDtoList.add(new PointDto(x/1000.0, y));
            }
            log.info("Throughput for taskId={} is: {}", taskId, pointDtoList);
        } finally {
            entityManager.close();
        }

        return pointDtoList;
    }

    @Override
    public List<PointDto> getPlotData(long taskId, String plotType) {
        Plot plot = Enum.valueOf(Plot.class, plotType.toUpperCase());

        if (plot == Plot.THROUGHPUT) {
            return getThroughputData(taskId);
        }

        throw new UnsupportedOperationException("Plot type " + plot + " doesn't supported");
    }
}
