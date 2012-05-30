package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class PlotNameDto implements Serializable {
    private long taskId;
    private String plotName;

    public PlotNameDto() {
    }

    public PlotNameDto(long taskId, String plotName) {
        this.taskId = taskId;
        this.plotName = plotName;
    }

    public long getTaskId() {
        return taskId;
    }

    public String getPlotName() {
        return plotName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlotNameDto)) return false;

        PlotNameDto that = (PlotNameDto) o;

        if (taskId != that.taskId) return false;
        if (plotName != null ? !plotName.equals(that.plotName) : that.plotName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (taskId ^ (taskId >>> 32));
        result = 31 * result + (plotName != null ? plotName.hashCode() : 0);
        return result;
    }
}
