package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.dto.PointDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class DataProcessingUtil {

    protected DataProcessingUtil() {
    }

    public static List<PointDto> convertFromRawDataToPointDto(List<Object[]> rawData) {
        if (rawData == null) {
            throw new IllegalArgumentException("rawData is null");
        }
        if (rawData.isEmpty()) {
            return Collections.emptyList();
        }

        List<PointDto> pointDtoList = new ArrayList<PointDto>(rawData.size());
        for (Object[] raw : rawData) {
            if (raw.length != 2) {
                throw new IllegalArgumentException("rawData must contains two elements array with coordinates");
            }

            double x = round((Long) raw[0] / 1000.0D);
            double y = round((Double) raw[1]);
            pointDtoList.add(new PointDto(x, y));
        }
        return pointDtoList;
    }

    public static double round(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }
}
