package com.griddynamics.jagger.webclient.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.griddynamics.jagger.webclient.client.PlotProviderService;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public class PlotProviderServiceImpl extends RemoteServiceServlet implements PlotProviderService {
    private static final Logger log = LoggerFactory.getLogger(PlotProviderServiceImpl.class);

    private final ThroughputPlotDataProvider throughputPlotDataProvider = new ThroughputPlotDataProvider();
    private final LatencyPlotDataProvider latencyPlotDataProvider = new LatencyPlotDataProvider();

    @Override
    public List<PlotNameDto> getPlotListForTask(long taskId) {
        List<PlotNameDto> plotNameDtoList = new ArrayList<PlotNameDto>(Plot.values().length);
        plotNameDtoList.add(new PlotNameDto(taskId, Plot.LATENCY.getText()));
        plotNameDtoList.add(new PlotNameDto(taskId, Plot.THROUGHPUT.getText()));

        return plotNameDtoList;
    }

    @Override
    public PlotSeriesDto getThroughputData(long taskId) {
        PlotSeriesDto plotSeriesDto = throughputPlotDataProvider.getPlotData(taskId);

        log.info("Throughput for taskId={} is: {}", taskId, plotSeriesDto);

        return plotSeriesDto;
    }

    @Override
    public PlotSeriesDto getLatencyData(long taskId) {
        PlotSeriesDto plotSeriesDto = latencyPlotDataProvider.getPlotData(taskId);
        log.info("Latency for taskId={} is: {}", taskId, plotSeriesDto);
        return plotSeriesDto;
    }

    @Override
    public PlotSeriesDto getPlotData(long taskId, String plotType) {
        Plot plot = Enum.valueOf(Plot.class, plotType.toUpperCase());

        if (plot == Plot.THROUGHPUT) {
            return getThroughputData(taskId);
        } else if (plot == Plot.LATENCY) {
            return getLatencyData(taskId);
        }

        throw new UnsupportedOperationException("Plot type " + plot + " doesn't supported");
    }
}
