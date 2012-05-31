package com.griddynamics.jagger.webclient.server;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public enum Plot {
    LATENCY("Latency"),
    THROUGHPUT("Throughput");

    private final String text;

    private Plot(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
