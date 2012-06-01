package com.griddynamics.jagger.webclient.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.griddynamics.jagger.webclient.client.PlotProviderService;
import com.griddynamics.jagger.webclient.client.dto.PlotDatasetDto;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        List<PlotNameDto> plotNameDtoList = new ArrayList<PlotNameDto>(Plot.values().length);
        plotNameDtoList.add(new PlotNameDto(taskId, Plot.LATENCY.getText()));
        plotNameDtoList.add(new PlotNameDto(taskId, Plot.THROUGHPUT.getText()));
        plotNameDtoList.add(new PlotNameDto(taskId, Plot.TIME_LATENCY_PERCENTILE.getText()));

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
    public PlotSeriesDto getPlotData(long taskId, String plotType) {
        long timestamp = System.currentTimeMillis();
        Plot plot = Plot.fromText(plotType);

        PlotSeriesDto plotSeriesDto = null;
        if (plot == Plot.THROUGHPUT) {
            plotSeriesDto = getThroughputData(taskId);
        } else if (plot == Plot.LATENCY) {
            plotSeriesDto = getLatencyData(taskId);
        } else if (plot == Plot.TIME_LATENCY_PERCENTILE) {
            plotSeriesDto = getTimeLatencyPercentileData(taskId);
        } else {
            throw new UnsupportedOperationException("Plot type " + plot + " doesn't supported");
        }

        Map<String, Integer> plotDatasetDtoMetrics = new HashMap<String, Integer>();
        for (PlotDatasetDto plotDatasetDto : plotSeriesDto.getPlotSeries()) {
            plotDatasetDtoMetrics.put(plotDatasetDto.getLegend(), plotDatasetDto.getPlotData().size());
        }
        log.info("For {} plot there was loaded {} PlotDatasetDto: {} for {} ms",
                new Object[] {plot.getText(), plotSeriesDto.getPlotSeries().size(), plotDatasetDtoMetrics, System.currentTimeMillis()-timestamp});

        return plotSeriesDto;
    }
}
