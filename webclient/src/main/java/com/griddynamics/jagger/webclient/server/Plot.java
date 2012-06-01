package com.griddynamics.jagger.webclient.server;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public enum Plot {
    LATENCY("Latency"),
    LATENCY_STD_DEV("Latency std deviation"),
    THROUGHPUT("Throughput"),
    TIME_LATENCY_PERCENTILE("Time Latency Percentile"),
    TIME_LATENCY_PERCENTILE_40("40"),
    TIME_LATENCY_PERCENTILE_50("40"),
    TIME_LATENCY_PERCENTILE_60("40"),
    TIME_LATENCY_PERCENTILE_70("40"),
    TIME_LATENCY_PERCENTILE_80("40"),
    TIME_LATENCY_PERCENTILE_90("40"),
    TIME_LATENCY_PERCENTILE_95("95"),
    TIME_LATENCY_PERCENTILE_99("99");

    private final String text;

    private Plot(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static Plot fromText(String text) {
        for (Plot plot : Plot.values()) {
            if (plot.getText().equalsIgnoreCase(text)) {
                return plot;
            }
        }
        return null;
    }
}
