package com.griddynamics.jagger.dbapi.fetcher;

import com.griddynamics.jagger.dbapi.util.ColorCodeGenerator;
import com.griddynamics.jagger.dbapi.dto.*;

import java.util.*;

public abstract class SummaryDbMetricDataFetcher extends DbMetricDataFetcher<MetricDto> {

    protected PlotDatasetDto generatePlotDatasetDto(MetricDto metricDto) {
        //So plot draws as {(0, val0),(1, val1), (2, val2), ... (n, valn)}
        List<PointDto> list = new ArrayList<PointDto>();

        List<MetricValueDto> metricList = new ArrayList<MetricValueDto>();
        for(MetricValueDto value :metricDto.getValues()) {
            metricList.add(value);
        }

        Collections.sort(metricList, new Comparator<MetricValueDto>() {

            @Override
            public int compare(MetricValueDto o1, MetricValueDto o2) {
                return  o2.getSessionId() < o1.getSessionId() ? 1 : -1;
            }
        });

        for (MetricValueDto value: metricList) {
            double temp = Double.parseDouble(value.getValue());
            list.add(new PointDto(value.getSessionId(), temp));
        }

        String legend = metricDto.getMetricName().getMetricDisplayName();

        return new PlotDatasetDto(
                list,
                legend,
                ColorCodeGenerator.getHexColorCode(metricDto.getMetricName().getMetricName(), new String())
        );
    }
}
