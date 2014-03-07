package com.griddynamics.jagger.webclient.server.fetch;


import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Abstract class to fetch Data from list of MetricNameDto
 * @param <R>
 */
public abstract class MetricDataFetcher<R> {

    protected List<MetricNameDto> metricNames = new ArrayList<MetricNameDto>();

    public void addMetricName(MetricNameDto metricName) {
        metricNames.add(metricName);
    }

    public void reset() {
        metricNames.clear();
    }

    public abstract Set<R> getResult();
}
