package com.griddynamics.jagger.dbapi.util;

import com.google.common.collect.ImmutableList;

import static com.griddynamics.jagger.util.MonitoringIdUtils.*;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class ColorCodeGenerator {
    private static AtomicInteger counter = new AtomicInteger(0);
    private static List<String> sessions = new Vector<String>();
    private static final  ImmutableList<String> colorsHexCodes = ImmutableList.of(
        "#FFD700",
        "#0000FF",
        "#8A2BE2",
        "#A52A2A",
        "#FF1493",
        "#000000",
        "#00BFFF",
        "#66CC33",
        "#FF7F50",
        "#FFCC33",
        "#DC143C",
        "#B8860B",
        "#006400",
        "#8B008B",
        "#FF8C00",
        "#FFFF00",
        "#5F9EA0",
        "#FF69B4",
        "#1E90FF",
        "#CD5C5C",
        "#FF0000",
        "#008000",
        "#D2691E",
        "#4B0082",
        "#9932CC",
        "#FF00FF",
        "#800000",
        "#00FF00",
        "#6495ED"
    );
    protected ColorCodeGenerator() {
    }

    public static String getHexColorCode() {
        int index = counter.getAndIncrement();
        return colorsHexCodes.get(index % colorsHexCodes.size());
    }

    public static String getHexColorCode(int id, String sessionId) {
        String colorId = id + sessionId;
        if (!sessions.contains(colorId))
            sessions.add(colorId);
        return colorsHexCodes.get(sessions.indexOf(colorId) % colorsHexCodes.size());
    }

    public static String getHexColorCode(String metricId, String sessionId) {
        String monitoringName = null;
        MonitoringId monitoringId = splitMonitoringMetricId(metricId);
        if (monitoringId != null)
            monitoringName = monitoringId.getMonitoringName();
        String colorId = monitoringName + sessionId;
        if (!sessions.contains(colorId))
            sessions.add(colorId);
        return colorsHexCodes.get(sessions.indexOf(colorId) % colorsHexCodes.size());
    }

}
