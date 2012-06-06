package com.griddynamics.jagger.webclient.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.griddynamics.jagger.agent.model.DefaultMonitoringParameters;
import com.griddynamics.jagger.monitoring.reporting.GroupKey;
import com.griddynamics.jagger.webclient.client.PlotProviderService;
import com.griddynamics.jagger.webclient.client.dto.PlotDatasetDto;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;
import com.griddynamics.jagger.webclient.client.dto.PointDto;
import com.griddynamics.jagger.webclient.server.plot.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.ArrayList;
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
    private DataPointCompressingProcessor compressingProcessor = new DataPointCompressingProcessor();

    @Override
    public List<PlotNameDto> getPlotListForTask(long taskId) {
        List<PlotNameDto> plotNameDtoList = new ArrayList<PlotNameDto>();

        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

        Map<GroupKey, DefaultWorkloadParameters[]> workloadPlots =
                (Map<GroupKey, DefaultWorkloadParameters[]>) context.getBean("workloadPlotGroups");
        for (Map.Entry<GroupKey, DefaultWorkloadParameters[]> monitoringPlot : workloadPlots.entrySet()) {
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
    public List<String> getSessionScopePlotList() {
        List<String> plotNameDtoList = new ArrayList<String>();

        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

        Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlots =
                (Map<GroupKey, DefaultMonitoringParameters[]>) context.getBean("monitoringPlotGroups");
        for (Map.Entry<GroupKey, DefaultMonitoringParameters[]> monitoringPlot : monitoringPlots.entrySet()) {
            plotNameDtoList.add(monitoringPlot.getKey().getUpperName());
        }

        return plotNameDtoList;
    }

    @Override
    public List<PlotSeriesDto> getPlotData(long taskId, String plotName) {
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

        List<PlotSeriesDto> plotSeriesDto = null;
        try {
            plotSeriesDto = plotDataProvider.getPlotData(taskId, plotName);
            log.info("getPlotData(): {}", getFormattedLogMessage(plotSeriesDto, "" + taskId, plotName));
        } catch (Exception e) {
            log.error("Error is occurred during plot data loading for taskId=" + taskId + ", plotName=" + plotName, e);
            throw new RuntimeException(e);
        }

        return plotSeriesDto;
    }

    @Override
    public List<PlotSeriesDto> getSessionScopePlotData(String sessionId, String plotName) {
        long timestamp = System.currentTimeMillis();
        log.debug("getPlotData was invoked with sessionId={} and plotName={}", sessionId, plotName);

        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

        Map<String, SessionScopePlotDataProvider> plotDataProviders =
                (Map<String, SessionScopePlotDataProvider>) context.getBean("monitoringPlotDataProviders");

        SessionScopePlotDataProvider plotDataProvider = plotDataProviders.get(plotName);
        if (plotDataProvider == null) {
            log.warn("getPlotData was invoked with unsupported plotName={}", plotName);
            throw new UnsupportedOperationException("Plot type " + plotName + " doesn't supported");
        }

        List<PlotSeriesDto> plotSeriesDto = null;
        try {
            plotSeriesDto = plotDataProvider.getPlotData(sessionId, plotName);
            log.info("getSessionScopePlotData(): {}", getFormattedLogMessage(plotSeriesDto, sessionId, plotName));
            for (PlotSeriesDto plotSeriesDto1 : plotSeriesDto) {
                for (PlotDatasetDto plotDatasetDto : plotSeriesDto1.getPlotSeries()) {
                    List<PointDto> pointDtoList = compressingProcessor.process(plotDatasetDto.getPlotData());
                    plotDatasetDto.getPlotData().clear();
                    plotDatasetDto.getPlotData().addAll(pointDtoList);
                }
            }
            log.info("getSessionScopePlotData() after compressing: {}", getFormattedLogMessage(plotSeriesDto, sessionId, plotName));
        } catch (Exception e) {
            log.error("Error is occurred during plot data loading for sessionId=" + sessionId + ", plotName=" + plotName, e);
            throw new RuntimeException(e);
        }

        return plotSeriesDto;
    }

    private String getFormattedLogMessage(List<PlotSeriesDto> plotSeriesDto, String id, String plotName) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("For id=")
                .append(id)
                .append(", plot name=\"")
                .append(plotName)
                .append("\" ")
                .append(plotSeriesDto.size())
                .append(" plots were found: ");
        for (PlotSeriesDto dto : plotSeriesDto) {
            logBuilder.append("\n* \"").append(dto.getPlotHeader()).append("\" {");

            int summaryPointsCount = 0;
            for (PlotDatasetDto plotDatasetDto : dto.getPlotSeries()) {
                summaryPointsCount += plotDatasetDto.getPlotData().size();
                logBuilder.append("\"")
                        .append(plotDatasetDto.getLegend())
                        .append("\" [")
                        .append(plotDatasetDto.getPlotData().size())
                        .append(" fetched data points], ");
            }
            logBuilder.append("} //Summary: ").append(summaryPointsCount).append(" points;");
        }

        return logBuilder.toString();
    }
}
