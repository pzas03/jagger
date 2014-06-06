package com.griddynamics.jagger.dbapi.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SummaryMetricDto implements Serializable {

    // trends plot
    private PlotSeriesDto plotSeriesDto;

    // all metrics suit for one metricNode
    private List<MetricDto> metricDtoList;

    public PlotSeriesDto getPlotSeriesDto() {
        return plotSeriesDto;
    }

    public void setPlotSeriesDto(PlotSeriesDto plotSeriesDto) {
        this.plotSeriesDto = plotSeriesDto;
    }

    public List<MetricDto> getMetricDtoList() {
        if (metricDtoList == null) {
            metricDtoList = new ArrayList<MetricDto>();
        }
        return metricDtoList;
    }

    public void setMetricDtoList(List<MetricDto> metricDtoList) {
        this.metricDtoList = metricDtoList;
    }
}
