package com.griddynamics.jagger.dbapi.util;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class LegendProvider {

    private static final String SESSION_PREFIX = "#";
    private static final String LEGEND_DESCRIPTION_SEPARATOR = ": ";

    public LegendProvider() {
    }

    public String generatePlotLegend(String sessionId, String description, boolean addSessionPrefix) {
        if (!addSessionPrefix) {
            return description;
        }

        return SESSION_PREFIX + sessionId + LEGEND_DESCRIPTION_SEPARATOR + description;
    }

    public String generatePlotHeader(String taskName, String plotName) {

        return taskName + ", " + plotName;
    }

    public static String parseMetricName(String legend) {
        if (legend.startsWith(SESSION_PREFIX)) {
            return legend.substring(legend.indexOf(LEGEND_DESCRIPTION_SEPARATOR) + LEGEND_DESCRIPTION_SEPARATOR.length());
        }
        return legend;
    }

    public static String parseSessionId(String legend) {
        if (legend.startsWith(SESSION_PREFIX)) {
            return legend.substring(legend.indexOf(SESSION_PREFIX), legend.indexOf(LEGEND_DESCRIPTION_SEPARATOR));
        }
        return "";
    }
}
