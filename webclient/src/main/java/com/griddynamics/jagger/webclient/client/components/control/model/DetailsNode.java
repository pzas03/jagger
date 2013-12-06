package com.griddynamics.jagger.webclient.client.components.control.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */
public class DetailsNode extends SimpleNode {

    SessionScopePlotsNode sessionScopePlotsNode;

    List<TestDetailsNode> tests;

    public DetailsNode() {}

    public DetailsNode(String id, String displayName) {
        super(id, displayName);
    }

    public SessionScopePlotsNode getSessionScopePlotsNode() {
        return sessionScopePlotsNode;
    }

    public void setSessionScopePlotsNode(SessionScopePlotsNode sessionScopePlotsNode) {
        this.sessionScopePlotsNode = sessionScopePlotsNode;
    }

    public List<TestDetailsNode> getTests() {
        return tests;
    }

    public void setTests(List<TestDetailsNode> tests) {
        this.tests = tests;
    }

    @Override
    public List<? extends SimpleNode> getChildren() {
        List<SimpleNode> result = new ArrayList<SimpleNode>();
        if (sessionScopePlotsNode != null) result.add(sessionScopePlotsNode);
        if (tests != null && !tests.isEmpty()) result.addAll(tests);
        return result;
    }
}
