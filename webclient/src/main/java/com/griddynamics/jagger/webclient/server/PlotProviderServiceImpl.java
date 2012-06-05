package com.griddynamics.jagger.webclient.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.griddynamics.jagger.agent.model.DefaultMonitoringParameters;
import com.griddynamics.jagger.monitoring.reporting.GroupKey;
import com.griddynamics.jagger.webclient.client.PlotProviderService;
import com.griddynamics.jagger.webclient.client.dto.PlotDatasetDto;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;
import com.griddynamics.jagger.webclient.server.plot.LatencyPlotDataProvider;
import com.griddynamics.jagger.webclient.server.plot.PlotDataProvider;
import com.griddynamics.jagger.webclient.server.plot.ThroughputPlotDataProvider;
import com.griddynamics.jagger.webclient.server.plot.TimeLatencyPercentilePlotDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public class PlotProviderServiceImpl extends RemoteServiceServlet implements PlotProviderService {
    private static final Logger log = LoggerFactory.getLogger(PlotProviderServiceImpl.class);

    private final PlotDataProvider throughputPlotDataProvider = new ThroughputPlotDataProvider();
    private final PlotDataProvider latencyPlotDataProvider = new LatencyPlotDataProvider();
    private final PlotDataProvider timeLatencyPercentilePlotDataProvider = new TimeLatencyPercentilePlotDataProvider();

    @Override
    public List<PlotNameDto> getPlotListForTask(long taskId) {
        List<PlotNameDto> plotNameDtoList = new ArrayList<PlotNameDto>();

        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

        Map<GroupKey, DefaultMonitoringParameters[]> workloadPlots =
                (Map<GroupKey, DefaultMonitoringParameters[]>) context.getBean("workloadPlotGroups");
        for (Map.Entry<GroupKey, DefaultMonitoringParameters[]> monitoringPlot : workloadPlots.entrySet()) {
            plotNameDtoList.add(new PlotNameDto(taskId, monitoringPlot.getKey().getUpperName()));
        }

        Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlots =
                (Map<GroupKey, DefaultMonitoringParameters[]>) context.getBean("monitoringPlotGroups");
        for (Map.Entry<GroupKey, DefaultMonitoringParameters[]> monitoringPlot : monitoringPlots.entrySet()) {
            plotNameDtoList.add(new PlotNameDto(taskId, monitoringPlot.getKey().getUpperName()));
        }

        return plotNameDtoList;
    }

    @Override
    public PlotSeriesDto getThroughputData(long taskId) {
        PlotSeriesDto plotSeriesDto = throughputPlotDataProvider.getPlotData(taskId);
        return plotSeriesDto;
    }

    @Override
    public PlotSeriesDto getLatencyData(long taskId) {
        PlotSeriesDto plotSeriesDto = latencyPlotDataProvider.getPlotData(taskId);
        return plotSeriesDto;
    }

    @Override
    public PlotSeriesDto getTimeLatencyPercentileData(long taskId) {
        PlotSeriesDto plotSeriesDto = timeLatencyPercentilePlotDataProvider.getPlotData(taskId);
        return plotSeriesDto;
    }

    @Override
    public PlotSeriesDto getPlotData(long taskId, String plotName) {
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

        Map<String, PlotDataProvider> workloadPlotDataProviders =
                (Map<String, PlotDataProvider>) context.getBean("workloadPlotDataProviders");

        long timestamp = System.currentTimeMillis();

        PlotDataProvider plotDataProvider = workloadPlotDataProviders.get(plotName);
        if (plotDataProvider == null) {
            throw new UnsupportedOperationException("Plot type " + plotName + " doesn't supported");
        }
        PlotSeriesDto plotSeriesDto = plotDataProvider.getPlotData(taskId);

        Map<String, Integer> plotDatasetDtoMetrics = new HashMap<String, Integer>();
        for (PlotDatasetDto plotDatasetDto : plotSeriesDto.getPlotSeries()) {
            plotDatasetDtoMetrics.put(plotDatasetDto.getLegend(), plotDatasetDto.getPlotData().size());
        }
        log.info("For {} plot there was loaded {} PlotDatasetDto: {} for {} ms",
                new Object[]{plotName, plotSeriesDto.getPlotSeries().size(), plotDatasetDtoMetrics, System.currentTimeMillis() - timestamp});

        return plotSeriesDto;
    }
}
