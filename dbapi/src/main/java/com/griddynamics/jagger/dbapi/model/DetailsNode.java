package com.griddynamics.jagger.dbapi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Corresponds to "Metric" tab in UI.
 * User: amikryukov
 * Date: 11/27/13
 */
public class DetailsNode extends AbstractIdentifyNode {

    List<TestDetailsNode> tests;
    private MetricGroupNode sessionScopeNode;

    public DetailsNode() {}

    public DetailsNode(String id, String displayName) {
        super(id, displayName);
    }

    public MetricGroupNode getSessionScopeNode() {
        return sessionScopeNode;
    }

    public void setSessionScopeNode(MetricGroupNode sessionScopeNode) {
        this.sessionScopeNode = sessionScopeNode;
    }

    public List<TestDetailsNode> getTests() {
        if (tests == null) {
            return Collections.EMPTY_LIST;
        }
        return tests;
    }

    public void setTests(List<TestDetailsNode> tests) {
        this.tests = tests;
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        List<AbstractIdentifyNode> result = new ArrayList<AbstractIdentifyNode>();
        if (sessionScopeNode != null) result.add(sessionScopeNode);
        if (tests != null && !tests.isEmpty()) result.addAll(tests);
        return result;
    }
}
