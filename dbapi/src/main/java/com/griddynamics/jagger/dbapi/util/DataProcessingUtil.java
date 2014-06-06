package com.griddynamics.jagger.dbapi.util;


import com.griddynamics.jagger.dbapi.dto.MetricDto;
import com.griddynamics.jagger.dbapi.dto.MetricValueDto;
import com.griddynamics.jagger.dbapi.dto.PlotDatasetDto;
import com.griddynamics.jagger.dbapi.dto.PointDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class DataProcessingUtil {

    protected DataProcessingUtil() {
    }

    public static List<PointDto> convertFromRawDataToPointDto(Collection<Object[]> rawData, int xIdx, int yIdx) {
        if (rawData == null) {
            throw new IllegalArgumentException("rawData is null");
        }
        if (rawData.isEmpty()) {
            return Collections.emptyList();
        }

        List<PointDto> pointDtoList = new ArrayList<PointDto>(rawData.size());
        for (Object[] raw : rawData) {
            double x = round((Long) raw[xIdx] / 1000.0D);
            double y = round((Double) raw[yIdx]);
            pointDtoList.add(new PointDto(x, y));
        }
        return pointDtoList;
    }

    public static double round(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    public static String getMessageFromLastCause(Throwable th) {
        if (th.getCause() != null)
            return getMessageFromLastCause(th.getCause());

        return th.getMessage();
    }

    /**
     * Generate Curve from values in MetricDto object as
     * {(sessionId1, val1),(sessionId2, val2), ... (sessionIdn, valn)}
     *
     * @param metricDto contains values to generate curve
     * @return curve
     */
    public static PlotDatasetDto generatePlotDatasetDto(MetricDto metricDto) {
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
                ColorCodeGenerator.getHexColorCode()
        );
    }
}

