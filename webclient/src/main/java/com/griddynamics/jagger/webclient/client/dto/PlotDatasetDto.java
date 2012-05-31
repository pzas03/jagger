package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class PlotDatasetDto implements Serializable {
    private List<PointDto> plotData = Collections.emptyList();
    private String legend;
    private String xAxisLabel;
    private String yAxisLabel;
    private String color;

    public PlotDatasetDto() {
    }

    public PlotDatasetDto(List<PointDto> plotData, String legend, String xAxisLabel, String yAxisLabel, String color) {
        this.plotData = plotData;
        this.legend = legend;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.color = color;
    }

    public List<PointDto> getPlotData() {
        return plotData;
    }

    public String getLegend() {
        return legend;
    }

    public String getXAxisLabel() {
        return xAxisLabel;
    }

    public String getYAxisLabel() {
        return yAxisLabel;
    }

    public String getColor() {
        return color;
    }
}
