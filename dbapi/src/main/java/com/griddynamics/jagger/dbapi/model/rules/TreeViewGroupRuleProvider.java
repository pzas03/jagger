package com.griddynamics.jagger.dbapi.model.rules;

import com.griddynamics.jagger.dbapi.parameter.DefaultMonitoringParameters;
import com.griddynamics.jagger.dbapi.parameter.GroupKey;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TreeViewGroupRuleProvider {

    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;

    public void setMonitoringPlotGroups(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
    }

    public TreeViewGroupRule provide (String rootId, String rootName) {

        List<TreeViewGroupRule> firstLevelFilters = new ArrayList<TreeViewGroupRule>();

        // Filter for Jagger main metrics. Space in display name will help to keep main parameters in the
        // top of alphabetic sorting
        //??? string
        StringBuilder builder = new StringBuilder(255);
        builder.append("(");
        builder.append(StandardMetricsNamesUtil.THROUGHPUT_TPS_REGEX).append("$|");
        builder.append(StandardMetricsNamesUtil.THROUGHPUT_REGEX).append("$|");
        builder.append(StandardMetricsNamesUtil.LATENCY_SEC_REGEX).append("$|");
        builder.append(StandardMetricsNamesUtil.LATENCY_REGEX).append("$|");
        builder.append(StandardMetricsNamesUtil.LATENCY_PERCENTILE_REGEX).append("$|");
        builder.append(StandardMetricsNamesUtil.ITERATIONS_SAMPLES_REGEX).append("$|");
        builder.append(StandardMetricsNamesUtil.SUCCESS_RATE_REGEX).append("$|");
        builder.append(StandardMetricsNamesUtil.DURATION_SEC_REGEX).append("$|");
        builder.append(StandardMetricsNamesUtil.TIME_LATENCY_PERCENTILE_REGEX).append("$|");
        builder.append(")");

        TreeViewGroupRule mainParams_FirstLevelFilter = new TreeViewGroupRule(Rule.By.DISPLAY_NAME,"main"," Main parameters",
                builder.toString());
        firstLevelFilters.add(mainParams_FirstLevelFilter);

        // Filters for Jagger monitoring parameters
        for(Map.Entry<GroupKey,DefaultMonitoringParameters[]> groupKeyEntry : monitoringPlotGroups.entrySet()) {
            String groupDisplayName = groupKeyEntry.getKey().getUpperName();

            String regex = "";
            for (DefaultMonitoringParameters defaultMonitoringParameters : groupKeyEntry.getValue()) {
                // not first / first time
                if (regex.length() != 0) {
                    regex += "|";
                }
                else {
                    regex += "^.*(";
                }
                regex += defaultMonitoringParameters.getId();
            }
            regex += ").*";

            firstLevelFilters.add(new TreeViewGroupRule(Rule.By.ID,groupDisplayName,groupDisplayName,regex));
        }

        // Root filter - will match all metrics
        return new TreeViewGroupRule(Rule.By.ID,rootId,rootName,".*",firstLevelFilters);
    }
}
