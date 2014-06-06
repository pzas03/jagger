package com.griddynamics.jagger.dbapi.model.rules;

import com.griddynamics.jagger.dbapi.parameter.DefaultMonitoringParameters;
import com.griddynamics.jagger.dbapi.parameter.GroupKey;
import com.griddynamics.jagger.util.StandardMetricsNamesUtil;

import java.util.ArrayList;
import java.util.Collection;
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
        String filterRegex = "(" +
        StandardMetricsNamesUtil.THROUGHPUT_TPS_REGEX + "$|" +
        StandardMetricsNamesUtil.THROUGHPUT_REGEX + "$|" +
        StandardMetricsNamesUtil.LATENCY_SEC_REGEX + "$|" +
        StandardMetricsNamesUtil.LATENCY_REGEX + "$|" +
        StandardMetricsNamesUtil.LATENCY_PERCENTILE_REGEX + "$|" +
        StandardMetricsNamesUtil.ITERATIONS_SAMPLES_REGEX + "$|" +
        StandardMetricsNamesUtil.SUCCESS_RATE_REGEX + "$|" +
        StandardMetricsNamesUtil.DURATION_SEC_REGEX + "$|" +
        StandardMetricsNamesUtil.TIME_LATENCY_PERCENTILE_REGEX + "$|" +
        ")";

        TreeViewGroupRule mainParams_FirstLevelFilter = new TreeViewGroupRule(Rule.By.DISPLAY_NAME,"main"," Main parameters",filterRegex);
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

    public TreeViewGroupRule provideWithPredefinedGroups(String rootId, String rootName,
                                                         Collection<String> legendGroups, String idFormatRegex) {

        List<TreeViewGroupRule> firstLevelFilters = new ArrayList<TreeViewGroupRule>();

        for (String legendGroup: legendGroups) {

            String regex = String.format(idFormatRegex, legendGroup);
            firstLevelFilters.add(new TreeViewGroupRule(Rule.By.ID, legendGroup, legendGroup, regex));
        }

        // Root filter - will match all metrics
        return new TreeViewGroupRule(Rule.By.ID,rootId,rootName,".*",firstLevelFilters);
    }
}
