package com.griddynamics.jagger.dbapi.model.rules;

import com.griddynamics.jagger.util.StandardMetricsNamesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TreeViewGroupStandardMetricsToNodeRuleProvider {

    public TreeViewGroupMetricsToNodeRule provide(Set<Double> percentiles) {
        List<TreeViewGroupMetricsToNodeRule> result = new ArrayList<TreeViewGroupMetricsToNodeRule>();

        // Throughput
        String regex = "^(" +
                StandardMetricsNamesUtil.THROUGHPUT_ID + "|" +
                StandardMetricsNamesUtil.THROUGHPUT_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX +
                ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.THROUGHPUT_ID,
                StandardMetricsNamesUtil.THROUGHPUT_TPS,
                regex));

        // Latency
        regex = "^(" +
                StandardMetricsNamesUtil.LATENCY_ID + "|" +
                StandardMetricsNamesUtil.LATENCY_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX +
                ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.LATENCY_ID,
                StandardMetricsNamesUtil.LATENCY_SEC,
                regex));

        // Latency, std dev
        regex = "^(" +
                StandardMetricsNamesUtil.LATENCY_STD_DEV_ID + "|" +
                StandardMetricsNamesUtil.LATENCY_STD_DEV_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX +
                ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.LATENCY_STD_DEV_ID,
                StandardMetricsNamesUtil.LATENCY_STD_DEV_SEC,
                regex));

        // Duration
        regex = "^(" +
                StandardMetricsNamesUtil.DURATION_ID + "|" +
                StandardMetricsNamesUtil.DURATION_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX +
                ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.DURATION_ID,
                StandardMetricsNamesUtil.DURATION_SEC,
                regex));


        // Fail count
        regex = "^(" +
                StandardMetricsNamesUtil.FAIL_COUNT_ID + "|" +
                StandardMetricsNamesUtil.FAIL_COUNT_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX +
                ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.FAIL_COUNT_ID,
                StandardMetricsNamesUtil.FAIL_COUNT,
                regex));

        // Samples
        regex = "^(" +
                StandardMetricsNamesUtil.ITERATION_SAMPLES_ID + "|" +
                StandardMetricsNamesUtil.ITERATION_SAMPLES_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX +
                ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.ITERATION_SAMPLES_ID,
                StandardMetricsNamesUtil.ITERATIONS_SAMPLES,
                regex));

        // Success rate
        regex = "^(" +
                StandardMetricsNamesUtil.SUCCESS_RATE_ID + "|" +
                StandardMetricsNamesUtil.SUCCESS_RATE_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX +
                ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.SUCCESS_RATE_ID,
                StandardMetricsNamesUtil.SUCCESS_RATE,
                regex));

        for (Double percentile : percentiles) {
            String reg = StandardMetricsNamesUtil.getLatencyMetricName(percentile);
            regex = "^(" +
                    reg + "|" +
                    reg + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX +
                    ")$";
            result.add(new TreeViewGroupMetricsToNodeRule(
                    Rule.By.ID,
                    reg,
                    reg,
                    regex));
        }

        return TreeViewGroupMetricsToNodeRule.Composer.compose(result);
    }
}
