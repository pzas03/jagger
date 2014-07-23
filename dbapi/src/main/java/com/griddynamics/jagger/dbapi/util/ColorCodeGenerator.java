package com.griddynamics.jagger.dbapi.util;

import com.google.common.collect.ImmutableList;
import com.griddynamics.jagger.util.MonitoringIdUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ColorCodeGenerator {
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

    public static String getHexColorCode(String metricId, String sessionId) {
        return getHexColorCode(Arrays.asList(metricId),sessionId);
    }

    public static String getHexColorCode(String metricId, List<String> synonyms,  String sessionId) {
        List<String> metricIds = new ArrayList<String>();
        metricIds.add(metricId);
        if (synonyms != null) {
            metricIds.addAll(synonyms);
        }
        return getHexColorCode(metricIds, sessionId);
    }

    private static String getHexColorCode(List<String> metricIds, String sessionId) {
        List<String> colorIds = new ArrayList<String>();

        if (!metricIds.isEmpty()) {
            // Search if metricId or its synonyms already has color
            for (String metricId : metricIds) {
                String colorId;
                MonitoringIdUtils.MonitoringId monitoringId = MonitoringIdUtils.splitMonitoringMetricId(metricId);
                if (monitoringId != null) {
                    colorId = monitoringId.getMonitoringName() + sessionId;
                } else {
                    colorId = metricId + sessionId;
                }
                colorIds.add(colorId);

                // Color found
                if (sessionsMap.containsKey(colorId)) {
                    Integer result = sessionsMap.get(colorId);

                    for (String id : colorIds) {
                        sessionsMap.put(id,result);
                    }
                    return colorsHexCodes.get(result);
                }
            }

        }

        // Color was not set before
        int index = Math.abs(counter.getAndIncrement() % colorsHexCodes.size());
        for (String id : colorIds) {
            sessionsMap.put(id, index);
        }
        return colorsHexCodes.get(index);
    }

}
