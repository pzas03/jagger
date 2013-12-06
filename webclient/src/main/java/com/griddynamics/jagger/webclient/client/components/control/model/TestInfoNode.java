package com.griddynamics.jagger.webclient.client.components.control.model;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/26/13
 */
public class TestInfoNode extends SimpleNode {

    List<TestInfoLeaf> testInfoList;

    @Deprecated
    public TestInfoNode() {}

    public TestInfoNode(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public List<TestInfoLeaf> getTestInfoList() {
        return testInfoList;
    }

    public void setTestInfoList(List<TestInfoLeaf> testInfoList) {
        this.testInfoList = testInfoList;
    }

    @Override
    public List<? extends SimpleNode> getChildren() {
        return testInfoList;
    }
}
