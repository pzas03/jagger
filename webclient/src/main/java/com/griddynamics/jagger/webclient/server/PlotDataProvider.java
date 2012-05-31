package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public interface PlotDataProvider {
    PlotSeriesDto getPlotData(long taskId);
}
