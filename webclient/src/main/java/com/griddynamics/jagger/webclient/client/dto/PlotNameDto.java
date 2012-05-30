package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class PlotNameDto implements Serializable {
    public static final int LATENCY_PLOT = 0;
    public static final int THROUGHPUT_PLOT = 1;
    private static final Map<Integer, String> map = new HashMap<Integer, String>();

    private long taskId;
    private int plotType;

    static {
        map.put(PlotNameDto.LATENCY_PLOT, "Latency");
        map.put(PlotNameDto.THROUGHPUT_PLOT, "Throughput");
    }

    public PlotNameDto() {
    }

    public PlotNameDto(long taskId, int plotType) {
        this.taskId = taskId;
        this.plotType = plotType;
    }

    public String getPlotName() {
        return map.get(plotType);
    }

    public int getPlotType() {
        return plotType;
    }

    public long getTaskId() {
        return taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlotNameDto)) return false;

        PlotNameDto plotNameDto = (PlotNameDto) o;

        if (plotType != plotNameDto.plotType) return false;
        if (taskId != plotNameDto.taskId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (taskId ^ (taskId >>> 32));
        result = 31 * result + plotType;
        return result;
    }
}
