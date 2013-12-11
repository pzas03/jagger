package com.griddynamics.jagger.webclient.client.components.control.model;

import java.util.Collections;
import java.util.List;

/**
 * User: amikryukov
 * Date: 11/26/13
 */
public class TestInfoLeaf extends AbstractIdentifyNode {

    public TestInfoLeaf() {}

    public TestInfoLeaf(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }
    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        return Collections.EMPTY_LIST;
    }
}
