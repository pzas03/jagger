package com.griddynamics.jagger.dbapi.util;

import com.google.common.collect.ImmutableList;

import static com.griddynamics.jagger.util.MonitoringIdUtils.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class ColorCodeGenerator {
    public static final int LATENCY_COLOR_ID_1 = 7;
    public static final int LATENCY_COLOR_ID_2 = 8;
    public static final int THROUGHPUT_COLOR_ID = 9;
    private static AtomicInteger counter = new AtomicInteger(0);
    private static ConcurrentMap<String, Integer> sessionsMap = new ConcurrentHashMap<String, Integer>();
    private static final  ImmutableList<String> colorsHexCodes = ImmutableList.of(
            "#000000",
            "#FF8C00",
            "#800000",
            "#FF4500",
            "#808000",
            "#00FF00",
            "#008000",
            "#00FFFF",
            "#008080",
            "#0000FF",
            "#000080",
            "#FF00FF",
            "#800080",
            "#D2691E",
            "#FF0000",
            "#00BFFF",
            "#9ACD32"
    );
    protected ColorCodeGenerator() {
    }

    public static String getHexColorCode() {
        int index = counter.getAndIncrement();
        return colorsHexCodes.get(index % colorsHexCodes.size());
    }

    public static String getHexColorCode(int id, String sessionId) {
        String colorId = id + sessionId;
        if (!sessionsMap.containsKey(colorId)) {
            sessionsMap.put(colorId, counter.getAndIncrement());
        }
        return colorsHexCodes.get(sessionsMap.get(colorId) % colorsHexCodes.size());
    }

    public static String getHexColorCode(String metricId, String sessionId) {
        String monitoringName = null;
        MonitoringId monitoringId = splitMonitoringMetricId(metricId);
        if (monitoringId != null) {
            monitoringName = monitoringId.getMonitoringName();
        }
        String colorId = monitoringName + sessionId;
        if (!sessionsMap.containsKey(colorId)) {
            sessionsMap.put(colorId, counter.getAndIncrement());
        }
        return colorsHexCodes.get(sessionsMap.get(colorId) % colorsHexCodes.size());
    }

}
