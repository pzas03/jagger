package com.griddynamics.jagger.webclient.server;

import java.util.ArrayList;
import java.util.List;

public class TreeViewGroupMetricsToNodeRuleProvider {

    public static TreeViewGroupMetricsToNodeRule provide () {
        //??? check unique ids for rules

        List<TreeViewGroupMetricsToNodeRule> result = new ArrayList<TreeViewGroupMetricsToNodeRule>();
        result.add(new TreeViewGroupMetricsToNodeRule("mysql_bytesMN","MySQL Bytes","^mysql_bytes.*"));

        return TreeViewGroupMetricsToNodeRule.Composer.compose(result);
    }
}
