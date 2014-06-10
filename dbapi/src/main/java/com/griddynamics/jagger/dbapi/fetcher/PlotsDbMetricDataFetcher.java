package com.griddynamics.jagger.dbapi.fetcher;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.PlotDatasetDto;
import com.griddynamics.jagger.dbapi.util.LegendProvider;
import com.griddynamics.jagger.util.Pair;

import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public abstract class PlotsDbMetricDataFetcher extends DbMetricDataFetcher<Pair<MetricNameDto, List<PlotDatasetDto>>> {

    protected LegendProvider legendProvider;

    @Required
    public void setLegendProvider(LegendProvider legendProvider) {
        this.legendProvider = legendProvider;
    }

}
