package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.data.MetricGroupRule;
import com.griddynamics.jagger.webclient.client.mvp.NameTokens;

import java.util.ArrayList;
import java.util.List;

public class TreeViewFilterRulesProvider {

    public static MetricGroupRule provide (String rootId, String rootName) {

        //??? temp examples
        List<MetricGroupRule> MySQL_SecondLevelFilters = new ArrayList<MetricGroupRule>();
        MySQL_SecondLevelFilters.add(new MetricGroupRule("innodb", "InnoDB", "^MySQL InnoDB .*"));
        MySQL_SecondLevelFilters.add(new MetricGroupRule("bytes", "Bytes", "^MySQL Bytes .*"));
        MySQL_SecondLevelFilters.add(new MetricGroupRule("quest", "Questions", "^MySQL Questions .*"));
        //??? will not find anything -> will be not displayed anywhere
        MySQL_SecondLevelFilters.add(new MetricGroupRule("dummy", "Dummy", "^MySQL bla bla bla .*"));

        MetricGroupRule MySQL_FirstLevelFilter = new MetricGroupRule("mysql","MySQL","^MySQL .*",MySQL_SecondLevelFilters);
        MetricGroupRule JMX_FirstLevelFilter = new MetricGroupRule("jmx","JMX","^JMX .*");


        // will take all metrics - root filter for test
        List<MetricGroupRule> FirstLevelFilters = new ArrayList<MetricGroupRule>();
        FirstLevelFilters.add(MySQL_FirstLevelFilter); //??? temp example
        FirstLevelFilters.add(JMX_FirstLevelFilter); //??? temp example
        return new MetricGroupRule(rootId,rootName,".*",FirstLevelFilters);
    }
}
