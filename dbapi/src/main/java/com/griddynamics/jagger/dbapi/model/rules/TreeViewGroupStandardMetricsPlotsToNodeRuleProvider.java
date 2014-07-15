package com.griddynamics.jagger.dbapi.model.rules;

import com.griddynamics.jagger.util.StandardMetricsNamesUtil;

import java.util.ArrayList;
import java.util.List;

public class TreeViewGroupStandardMetricsPlotsToNodeRuleProvider {

    public TreeViewGroupMetricsToNodeRule provide() {

        List<TreeViewGroupMetricsToNodeRule> result = new ArrayList<TreeViewGroupMetricsToNodeRule>();

        // Throughput
        String regex = "^(" +
                StandardMetricsNamesUtil.THROUGHPUT_ID + "|" +
                StandardMetricsNamesUtil.THROUGHPUT_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX +
                ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.THROUGHPUT_ID,
                StandardMetricsNamesUtil.THROUGHPUT,
                regex));

        // Latency
        regex = "^(" +
                StandardMetricsNamesUtil.LATENCY_ID + "|" +
                StandardMetricsNamesUtil.LATENCY_STD_DEV_ID + "|" +
                StandardMetricsNamesUtil.LATENCY_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX + "|" +
                StandardMetricsNamesUtil.LATENCY_STD_DEV_ID + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX +
                ")$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.LATENCY_ID + StandardMetricsNamesUtil.LATENCY_STD_DEV_ID,
                StandardMetricsNamesUtil.LATENCY,
                regex));

        // Time Latency Percentile
        regex = "^" + StandardMetricsNamesUtil.LATENCY_PERCENTILE_REGEX + StandardMetricsNamesUtil.STANDARD_METRICS_AS_CUSTOM_SUFFIX + "|" +
                StandardMetricsNamesUtil.LATENCY_PERCENTILE_REGEX + "$";
        result.add(new TreeViewGroupMetricsToNodeRule(
                Rule.By.ID,
                StandardMetricsNamesUtil.TIME_LATENCY_PERCENTILE + "_id",
                StandardMetricsNamesUtil.TIME_LATENCY_PERCENTILE,
                regex));

        return TreeViewGroupMetricsToNodeRule.Composer.compose(result);
    }
}
