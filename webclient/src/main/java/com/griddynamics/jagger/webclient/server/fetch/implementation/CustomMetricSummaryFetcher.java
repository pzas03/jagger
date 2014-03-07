package com.griddynamics.jagger.webclient.server.fetch.implementation;

import com.griddynamics.jagger.webclient.client.dto.MetricDto;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.server.fetch.DbMetricDataFetcher;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CustomMetricSummaryFetcher extends DbMetricDataFetcher<MetricDto> {

    @Override
    protected Set<MetricDto> fetchData(List<MetricNameDto> metricNames) {

        // temporary stab
        return Collections.EMPTY_SET;
    }
}
