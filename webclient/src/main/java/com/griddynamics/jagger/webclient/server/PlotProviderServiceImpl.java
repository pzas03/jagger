package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.PlotProviderService;
import com.griddynamics.jagger.webclient.client.components.control.model.MetricNode;
import com.griddynamics.jagger.webclient.client.dto.*;
import com.griddynamics.jagger.webclient.server.plot.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public class PlotProviderServiceImpl implements PlotProviderService {
    private static final Logger log = LoggerFactory.getLogger(PlotProviderServiceImpl.class);

    private DataPointCompressingProcessor compressingProcessor;
    private ThroughputPlotDataProvider throughputPlotDataProvider;
    private LatencyPlotDataProvider latencyPlotDataProvider;
    private TimeLatencyPercentilePlotDataProvider timeLatencyPercentilePlotDataProvider;
    private MonitoringPlotDataProvider monitoringPlotDataProvider;
    private CustomMetricPlotDataProvider customMetricPlotDataProvider;

    //==========Setters

    @Required
    public void setCompressingProcessor(DataPointCompressingProcessor compressingProcessor) {
        this.compressingProcessor = compressingProcessor;
    }

    @Required
    public void setCustomMetricPlotDataProvider(CustomMetricPlotDataProvider customMetricPlotDataProvider) {
        this.customMetricPlotDataProvider = customMetricPlotDataProvider;
    }

    @Required
    public void setThroughputPlotDataProvider(ThroughputPlotDataProvider throughputPlotDataProvider) {
        this.throughputPlotDataProvider = throughputPlotDataProvider;
    }

    @Required
    public void setLatencyPlotDataProvider(LatencyPlotDataProvider latencyPlotDataProvider) {
        this.latencyPlotDataProvider = latencyPlotDataProvider;
    }

    @Required
    public void setTimeLatencyPercentilePlotDataProvider(TimeLatencyPercentilePlotDataProvider timeLatencyPercentilePlotDataProvider) {
        this.timeLatencyPercentilePlotDataProvider = timeLatencyPercentilePlotDataProvider;
    }

    @Required
    public void setMonitoringPlotDataProvider(MonitoringPlotDataProvider monitoringPlotDataProvider) {
        this.monitoringPlotDataProvider = monitoringPlotDataProvider;
    }

    //===========================
    //===========Contract Methods
    //===========================

//    //??? not used @Override
//    private List<PlotSeriesDto> getPlotData(long taskId, MetricNameDto plotName) {
//        long timestamp = System.currentTimeMillis();
//        log.debug("getPlotData was invoked with taskId={} and metricName={}", taskId, plotName);
//
//        PlotDataProvider plotDataProvider = findPlotDataProvider(plotName);
//
//        List<PlotSeriesDto> plotSeriesDto;
//        try {
//            plotSeriesDto = plotDataProvider.getPlotData(taskId, plotName);
//            log.info("getPlotData(): {}", getFormattedLogMessage(plotSeriesDto, "" + taskId, plotName.getMetricName(), System.currentTimeMillis() - timestamp));
//        } catch (Exception e) {
//            log.error("Error is occurred during plot data loading for taskId=" + taskId + ", metricName=" + plotName, e);
//            throw new RuntimeException(e);
//        }
//
//        return plotSeriesDto;
//    }

    //??? not used    @Override
    private List<PlotSeriesDto> getPlotData(MetricNameDto metricNameDto) {
        long timestamp = System.currentTimeMillis();
        log.debug("getPlotData was invoked with taskIds={} and metricNameDto={}", metricNameDto.getTaskIds(), metricNameDto);

        PlotDataProvider plotDataProvider = findPlotDataProvider(metricNameDto);

        List<PlotSeriesDto> plotSeriesDtoList;
        try {
            plotSeriesDtoList = plotDataProvider.getPlotData(metricNameDto);
            log.info("getPlotData(): {}", getFormattedLogMessage(plotSeriesDtoList, "" + metricNameDto.getTaskIds(), metricNameDto.getMetricName(), System.currentTimeMillis() - timestamp));
        } catch (Exception e) {
            log.error("Error is occurred during plot data loading for taskIds=" + metricNameDto.getTaskIds() + ", metricNameDto=" + metricNameDto, e);
            throw new RuntimeException(e);
        }

        return plotSeriesDtoList;
    }

    @Override
    public Map<MetricNameDto, List<PlotSeriesDto>> getPlotDatas(Set<MetricNameDto> plots) throws IllegalArgumentException{
        Map<MetricNameDto,List<PlotSeriesDto>> result = new LinkedHashMap<MetricNameDto, List<PlotSeriesDto>>(plots.size());
        // todo : fetch metrics  plots in one query
        for (MetricNameDto plot : plots){
            result.put(plot, getPlotData(plot));
        }
        return result;
    }

    //??? dummy
    @Override
    public Map<MetricNameDto, List<PlotSeriesDto>> getPlotDatas(Set<MetricNode> plots, boolean dummy) throws IllegalArgumentException{
//        Map<MetricNameDto,List<PlotSeriesDto>> result = new LinkedHashMap<MetricNameDto, List<PlotSeriesDto>>(plots.size());
//
//        long timestamp = System.currentTimeMillis();
//        log.debug("getPlotData was invoked with metricNode={}", plots);
//
//        //??? only for custom metrics
//        PlotDataProvider plotDataProvider = customMetricPlotDataProvider;
//        List<PlotSeriesDto> plotSeriesDtoList;
//
//        for (MetricNode plot : plots) {
//            try {
//                //???
//                plotSeriesDtoList = plotDataProvider.getPlotData(taskIds, plotName);
//                log.info("getPlotData(): {}", getFormattedLogMessage(plotSeriesDtoList, "" + taskIds, plotName.getMetricName(), System.currentTimeMillis() - timestamp));
//            } catch (Exception e) {
//                log.error("Error is occurred during plot data loading for taskIds=" + taskIds + ", metricName=" + plotName, e);
//                throw new RuntimeException(e);
//            }
//        }
//
//        ???
//
//        //???
//        // todo : fetch metrics  plots in one query
//        for (MetricNameDto plot : plots){
//            result.put(plot, getPlotData(plot.getTaskIds(), plot));
//        }
//
//
//
//
//
//        return plotSeriesDtoList;
//
//
//        //???
////        Map<MetricNameDto,List<PlotSeriesDto>> result = new LinkedHashMap<MetricNameDto, List<PlotSeriesDto>>(plots.size());
////        // todo : fetch metrics  plots in one query
////        for (MetricNameDto plot : plots){
////            result.put(plot, getPlotData(plot.getTaskIds(), plot));
////        }
////        return result;
//
//        //???
        return null;
    }


    @Override
    public Map<SessionPlotNameDto, List<PlotSeriesDto>> getSessionScopePlotData(String sessionId, Collection<SessionPlotNameDto> plotNames) {
        long timestamp = System.currentTimeMillis();
        Map<SessionPlotNameDto, List<PlotSeriesDto>> resultMap = new HashMap<SessionPlotNameDto, List<PlotSeriesDto>>();

        SessionScopePlotDataProvider plotDataProvider = monitoringPlotDataProvider;

        for(SessionPlotNameDto plotName : plotNames) {
            log.debug("getPlotData was invoked with sessionId={} and metricName={}", sessionId, plotName);
            List<PlotSeriesDto> plotSeriesDtoList;

            try {
                plotSeriesDtoList = plotDataProvider.getPlotData(sessionId, plotName.getMetricName());
                log.info("getSessionScopePlotData(): {}", getFormattedLogMessage(plotSeriesDtoList, sessionId, plotName.getMetricName(), System.currentTimeMillis() - timestamp));
                for (PlotSeriesDto plotSeriesDto : plotSeriesDtoList) {
                    for (PlotDatasetDto plotDatasetDto : plotSeriesDto.getPlotSeries()) {
                        List<PointDto> pointDtoList = compressingProcessor.process(plotDatasetDto.getPlotData());
                        plotDatasetDto.getPlotData().clear();
                        plotDatasetDto.getPlotData().addAll(pointDtoList);
                    }
                }
                log.info("getSessionScopePlotData() after compressing: {}", getFormattedLogMessage(plotSeriesDtoList, sessionId, plotName.getMetricName(), System.currentTimeMillis() - timestamp));
            } catch (Exception e) {
                System.err.println(e);
                log.error("Error is occurred during plot data loading for sessionId=" + sessionId + ", metricName=" + plotName, e);
                throw new RuntimeException(e);
            }
            resultMap.put(plotName, plotSeriesDtoList);
        }

        return resultMap;
    }

    //===========================
    //==========Auxiliary Methods
    //===========================

    private String getFormattedLogMessage(List<PlotSeriesDto> plotSeriesDto, String id, String plotName, long millis) {
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
            logBuilder.append("\nExecuted for ").append(millis).append(" ms");
        }

        return logBuilder.toString();
    }

    private PlotDataProvider findPlotDataProvider(MetricNameDto metricNameDto) {
        PlotDataProvider plotDataProvider = null;
        switch (metricNameDto.getOrigin()) {
            case UNKNOWN:
            case STANDARD_METRICS:
            case VALIDATOR_NEW_MODEL:
            case VALIDATOR_OLD_MODEL:
            case DURATION:

                //??? exception here

                break;
            case METRIC_NEW_MODEL:
            case METRIC_OLD_MODEL:
                plotDataProvider = customMetricPlotDataProvider;
                break;
            case LATENCY:
                plotDataProvider = latencyPlotDataProvider;
                break;
            case THROUGHPUT:
                plotDataProvider = throughputPlotDataProvider;
                break;
            case LATENCY_PERCENTILE:
                plotDataProvider = timeLatencyPercentilePlotDataProvider;
                break;
            case MONITORING:
                plotDataProvider = monitoringPlotDataProvider;
                break;
        }

        return plotDataProvider;
    }
}
