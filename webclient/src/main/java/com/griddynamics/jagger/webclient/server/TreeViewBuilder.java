package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.components.control.model.MetricGroupNode;
import com.griddynamics.jagger.webclient.client.components.control.model.MetricNode;
import com.griddynamics.jagger.webclient.client.data.MetricGroupRule;
import com.griddynamics.jagger.webclient.client.mvp.NameTokens;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TreeViewBuilder<M extends MetricNode> {

//    public MetricGroupNode<M> fillMetricGroupNode(MetricGroupRule metricGroupRule, NameTokens.FilterOptions filterBy, String parentId, List<M> metricNodeList) {
//        MetricGroupNode result = new MetricGroupNode<M>(metricGroupRule.getDisplayName());
//        boolean returnResult = false;
//
//        // null is required only for root node
//        // when no parent available
//        String id = "";
//        if (parentId == null) {
//            id = metricGroupRule.getId();
//        }
//        else {
//            id = parentId + "_" + metricGroupRule.getId();
//        }
//
//        // unique Id for metric group
//        result.setId(id);
//
//        // first ask all children to filter
//        List<MetricGroupNode> metricGroupNodeListFromChildren = new ArrayList<MetricGroupNode>();
//        List<MetricGroupRule> metricGroupRuleChildrens = metricGroupRule.getChildren();
//        if (metricGroupRuleChildrens != null) {
//            for (MetricGroupRule child : metricGroupRuleChildrens) {
//                MetricGroupNode childResult = child.filter(filterBy,id,metricNodeList);
//                if (childResult != null) {
//                    metricGroupNodeListFromChildren.add(childResult);
//                }
//            }
//            if (metricGroupNodeListFromChildren.size() > 0) {
//                result.setMetricGroupNodeList(metricGroupNodeListFromChildren);
//                returnResult = true;
//            }
//        }
//
//        // apply own filter
//        if (rule != null) {
//            List<MetricNode> metricsPerRule = new ArrayList<MetricNode>();
//            Iterator<MetricNode> i = metricNodeList.iterator();
//            while (i.hasNext()) {
//                MetricNode metricNode = i.next();
//
//                // match
//                String metric = "";
//                if (filterBy == NameTokens.FilterOptions.BY_DISPLAY_NAME) {
//                    metric = metricNode.getMetricNameDto().getDisplayName();
//                }
//                else {
//                    metric = metricNode.getMetricNameDto().getName();
//                }
//                if (metric.matches(rule)) {
//                    metricsPerRule.add(metricNode);
//                    i.remove();
//                }
//            }
//            if (metricsPerRule.size() > 0) {
//                result.setMetrics(metricsPerRule);
//                returnResult = true;
//            }
//        }
//
//        if (returnResult) {
//            return result;
//        }
//        else {
//            return null;
//        }
//    }
}
