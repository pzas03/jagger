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
    public PlotSeriesDto getPlotData(long taskId, String plotName) {
        long timestamp = System.currentTimeMillis();
        log.debug("getPlotData was invoked with taskId={} and plotName={}", taskId, plotName);

        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

        Map<String, PlotDataProvider> plotDataProviders =
                (Map<String, PlotDataProvider>) context.getBean("workloadPlotDataProviders");
        plotDataProviders.putAll((Map<String, PlotDataProvider>) context.getBean("monitoringPlotDataProviders"));

        PlotDataProvider plotDataProvider = plotDataProviders.get(plotName);
        if (plotDataProvider == null) {
            log.warn("getPlotData was invoked with unsupported plotName={}", plotName);
            throw new UnsupportedOperationException("Plot type " + plotName + " doesn't supported");
        }

        PlotSeriesDto plotSeriesDto = null;
        try {
            plotSeriesDto = plotDataProvider.getPlotData(taskId, plotName);

            Map<String, Integer> plotDatasetDtoMetrics = new HashMap<String, Integer>();
            for (PlotDatasetDto plotDatasetDto : plotSeriesDto.getPlotSeries()) {
                plotDatasetDtoMetrics.put(plotDatasetDto.getLegend(), plotDatasetDto.getPlotData().size());
            }
            log.info("For {} plot there was loaded {} PlotDatasetDto: {} for {} ms",
                    new Object[]{plotName, plotSeriesDto.getPlotSeries().size(), plotDatasetDtoMetrics, System.currentTimeMillis() - timestamp});
        } catch (Exception e) {
            log.error("Error is occurred during plot data loading for taskId="+taskId+", plotName="+plotName, e);
            throw new RuntimeException(e);
        }

        return plotSeriesDto;
    }
}
