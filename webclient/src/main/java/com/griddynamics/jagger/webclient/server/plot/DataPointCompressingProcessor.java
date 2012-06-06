package com.griddynamics.jagger.webclient.server.plot;

import com.griddynamics.jagger.util.Pair;
import com.griddynamics.jagger.webclient.client.dto.PointDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/6/12
 */
public class DataPointCompressingProcessor {
    private static final Logger log = LoggerFactory.getLogger(DataPointCompressingProcessor.class);
    private double relativeThreshold = 0.03;

    public List<PointDto> process(List<PointDto> source) {
        if (source.size() < 3) {
            List<PointDto> list = new ArrayList<PointDto>();
            list.addAll(source);
            return list;
        }

        List<PointDto> compressed = new ArrayList<PointDto>();

        // Sort by X axis values asc
        Collections.sort(source, new Comparator<PointDto>() {
            @Override
            public int compare(PointDto o1, PointDto o2) {
                return Double.compare(o1.getX(), o2.getX());
            }
        });

        Pair<Double, Double> minMaxPair = findMinMax(source);
        double absoluteThreshold = Math.abs((minMaxPair.getSecond() - minMaxPair.getFirst()) * relativeThreshold);
        log.debug("min={}, max={}, threshold={}", new Object[] {minMaxPair.getFirst(), minMaxPair.getSecond(), absoluteThreshold});

        int i = 1;
        compressed.add(source.get(0));
        while (true) {
            // If ith element is the last in source list
            if (i == source.size() - 1) {
                compressed.add(source.get(i));
                break;
            }

            // If 2==1 and 2==3 then 2 throw from compressed list
            double current = source.get(i).getY();
            double previous = compressed.get(compressed.size() - 1).getY();
            double next = source.get(i + 1).getY();
            if (isThresholdExceeded(Pair.of(current, previous), absoluteThreshold) || isThresholdExceeded(Pair.of(current, next), absoluteThreshold)) {
                compressed.add(source.get(i));
            }
            i++;
        }

        return compressed;
    }

    private Pair<Double, Double> findMinMax(List<PointDto> pointDtoList) {
        if (pointDtoList.isEmpty()) {
            return null;
        }
        if (pointDtoList.size() == 1) {
            Double element = pointDtoList.get(0).getY();
            return Pair.of(element, element);
        }
        Iterator<PointDto> iter = pointDtoList.iterator();
        Double min = iter.next().getY();
        Double max = iter.next().getY();

        if (min.compareTo(max) > 0) {
            Double t = min;
            min = max;
            max = t;
        }
        while (iter.hasNext()) {
            Double value = iter.next().getY();
            if (value.compareTo(max) > 0) {
                max = value;
            } else if (value.compareTo(min) < 0) {
                min = value;
            }
        }

        return Pair.of(min, max);
    }

    private boolean isThresholdExceeded(Pair<Double, Double> pair, double threshold) {
        return Double.compare(Math.abs(pair.getFirst() - pair.getSecond()), threshold) > 0;
    }
}
