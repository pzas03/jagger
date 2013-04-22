package com.griddynamics.jagger.webclient.client.data;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 22.04.13
 * Time: 10:15
 * To change this template use File | Settings | File Templates.
 */
public class MetricRankingProvider {
    private static List<String> patterns = Arrays.asList(
            "Iterations",
            "Duration",
            "Throughput",
            "Latency",
            "Success rate",
            "Latency .+ %"
    );

    public static int compare(String o1, String o2){
        Comparable o1Rank = getRank(o1);
        Comparable o2Rank = getRank(o2);
        if (o1Rank.compareTo(0)==0 && o2Rank.compareTo(0)==0){
            return o1.compareTo(o2);
        }
        return o1Rank.compareTo(o2Rank);
    }

    public static Comparable getRank(String o){
        int rank = 0;
        for (String pattern : patterns){
            if (pattern.matches(o)){
                return new Integer(patterns.size()-rank);
            }
            rank++;
        }
        return 0;
    }
}
