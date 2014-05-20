package com.griddynamics.jagger.util;

import java.util.Arrays;
import java.util.List;

public class MetricNamesRankingProvider {
    private static List<String> patterns = Arrays.asList(
            StandardMetricsNamesUtil.ITERATIONS_SAMPLES_REGEX.concat(".*"),
            StandardMetricsNamesUtil.DURATION_SEC_REGEX.concat(".*"),
            StandardMetricsNamesUtil.THROUGHPUT_REGEX.concat(".*"),
            StandardMetricsNamesUtil.SUCCESS_RATE_REGEX.concat(".*"),
            StandardMetricsNamesUtil.LATENCY_REGEX.concat(".*"),
            StandardMetricsNamesUtil.TIME_LATENCY_PERCENTILE_REGEX.concat(".*")
    );

    protected static int compare(String o1, String o2){
        Comparable o1Rank = getRank(o1);
        Comparable o2Rank = getRank(o2);
        if (o1Rank.compareTo(0)==0 && o2Rank.compareTo(0)==0){
            // display names, not matched to pattern above
            int res = String.CASE_INSENSITIVE_ORDER.compare(o1,o2);
            return (res != 0) ? res : o1.compareTo(o2);
        }
        if (o1Rank.compareTo(o2Rank)==0){
            return o1.compareTo(o2);
        }
        return o1Rank.compareTo(o2Rank);
    }

    protected static Comparable getRank(String o){
        int rank = 0;
        for (String pattern : patterns){
            if (o.matches(pattern)){
                return new Integer(-patterns.size()+rank);
            }
            rank++;
        }
        return 0;
    }
}
