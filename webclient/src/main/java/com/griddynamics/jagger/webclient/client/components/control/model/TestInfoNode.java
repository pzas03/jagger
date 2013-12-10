package com.griddynamics.jagger.webclient.client.components.control.model;

import java.util.List;

/**
 * User: amikryukov
 * Date: 11/26/13
 */
public class TestInfoNode extends AbstractIdentifyNode {

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
    public List<? extends AbstractIdentifyNode> getChildren() {
        return testInfoList;
    }
}
