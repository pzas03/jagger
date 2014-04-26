package com.griddynamics.jagger.engine.e1.services.data.service;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;

//??? docu
/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/5/13
 * Time: 12:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class MetricEntity {
    private MetricNameDto metricNameDto;
    private boolean summaryAvailable = false;
    private boolean plotAvailable = false;

    public void setMetricNameDto(MetricNameDto metricNameDto) {
        this.metricNameDto = metricNameDto;
    }
    public MetricNameDto getMetricNameDto() {
        return metricNameDto;
    }

    public String getMetricId() {
        return metricNameDto.getMetricName();
    }

    public String getDisplayName() {
        return metricNameDto.getMetricDisplayName();
    }

    public MetricNameDto.Origin getOrigin() {
        return metricNameDto.getOrigin();
    }

    public boolean isSummaryAvailable() {
        return summaryAvailable;
    }

    public void setSummaryAvailable(boolean summaryAvailable) {
        this.summaryAvailable = summaryAvailable;
    }

    public boolean isPlotAvailable() {
        return plotAvailable;
    }

    public void setPlotAvailable(boolean plotAvailable) {
        this.plotAvailable = plotAvailable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetricEntity that = (MetricEntity) o;

        if (plotAvailable != that.plotAvailable) return false;
        if (summaryAvailable != that.summaryAvailable) return false;
        if (!metricNameDto.equals(that.metricNameDto)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = metricNameDto.hashCode();
        result = 31 * result + (summaryAvailable ? 1 : 0);
        result = 31 * result + (plotAvailable ? 1 : 0);
        return result;
    }
}
