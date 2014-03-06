package com.griddynamics.jagger.webclient.server.rules;

import com.griddynamics.jagger.webclient.client.components.control.model.MetricNode;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.mvp.NameTokens;

import java.util.*;

public class TreeViewGroupMetricsToNodeRule extends Rule {

    public TreeViewGroupMetricsToNodeRule() {}
    public TreeViewGroupMetricsToNodeRule(String id, String displayName, String rule) {
        super(id,displayName,rule);
    }

    public <M extends MetricNode> List<M> filter(NameTokens.Filter filterBy, String parentId, List<M> metricNodeList) {

        List<M> result = new ArrayList<M>();
        M resultMetricNode = null;
        List<MetricNameDto> metricNameDtoList = new ArrayList<MetricNameDto>();

        Iterator<M> i = metricNodeList.iterator();
        while (i.hasNext()) {
            M metricNode = i.next();

            // match
            String metric;
            // node can contain more than single metric
            // current strategy: if at least one metric match => take it
            for (MetricNameDto metricNameDto : metricNode.getMetricNameDtoList()) {
                if (filterBy == NameTokens.Filter.BY_DISPLAY_NAME) {
                    metric = metricNameDto.getMetricDisplayName();
                }
                else {
                    metric = metricNameDto.getMetricName();
                }
                if (metric.matches(rule)) {
                    //create result single time
                    if (resultMetricNode == null) {
                        resultMetricNode = metricNode;
                    }
                    metricNameDtoList.addAll(metricNode.getMetricNameDtoList());
                    i.remove();
                    break;
                }
            }
        }

        // result - compose single node from filtered list
        if (resultMetricNode != null) {
            // N goes for node
            resultMetricNode.init(parentId + "N_" + id,displayName,metricNameDtoList);
            result.add(resultMetricNode);
            return result;
        }
        else {
            return null;
        }
    }

    public static class Composer extends TreeViewGroupMetricsToNodeRule {
        private List<TreeViewGroupMetricsToNodeRule> treeViewGroupMetricsToNodeRules;

        public Composer(List<TreeViewGroupMetricsToNodeRule> treeViewGroupMetricsToNodeRules) {
            this.treeViewGroupMetricsToNodeRules = sortByDisplayName(treeViewGroupMetricsToNodeRules);
        }

        public static TreeViewGroupMetricsToNodeRule compose(List<TreeViewGroupMetricsToNodeRule> treeViewGroupMetricsToNodeRules){
            return new Composer(treeViewGroupMetricsToNodeRules);
        }

        @Override
        public <M extends MetricNode> List<M> filter(NameTokens.Filter filterBy, String parentId, List<M> metricNodeList) {
            List<M> result = new ArrayList<M>();

            List<M> tempResult = null;
            for (TreeViewGroupMetricsToNodeRule rule : treeViewGroupMetricsToNodeRules) {
                tempResult = rule.filter(filterBy,parentId,metricNodeList);
                if (tempResult != null) {
                    result.addAll(tempResult);
                }
            }

            if (result.size() > 0) {
                return result;
            }
            else {
                return null;
            }
        }
    }

    //???
    protected List<TreeViewGroupMetricsToNodeRule> sortByDisplayName(List<TreeViewGroupMetricsToNodeRule> metricGroupRuleList) {
        Collections.sort(metricGroupRuleList, new Comparator<TreeViewGroupMetricsToNodeRule>() {
            @Override
            public int compare(TreeViewGroupMetricsToNodeRule o1, TreeViewGroupMetricsToNodeRule o2) {
                int res = String.CASE_INSENSITIVE_ORDER.compare(o1.getDisplayName(), o2.getDisplayName());
                return (res != 0) ? res : o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });

        return metricGroupRuleList;
    }
}
