package com.griddynamics.jagger.webclient.client.components.control.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/26/13
 */
public class SummaryNode extends SimpleNode {

    List<TestNode> tests;

    SessionInfoNode sessionInfo;

    public SummaryNode() {
    }

    public SummaryNode(String id, String displayName) {
        super(id, displayName);
    }

    public List<TestNode> getTests() {
        return tests;
    }

    public void setTests(List<TestNode> tests) {
        this.tests = tests;
    }

    public SessionInfoNode getSessionInfo() {
        return sessionInfo;
    }

    public void setSessionInfo(SessionInfoNode sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    @Override
    public List<? extends SimpleNode> getChildren() {
        ArrayList<SimpleNode> result = new ArrayList<SimpleNode>();
        result.add(sessionInfo);
        if (tests != null && !tests.isEmpty())result.addAll(tests);
        return result;
    }
}
