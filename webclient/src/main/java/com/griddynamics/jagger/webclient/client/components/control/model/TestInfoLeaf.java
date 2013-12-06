package com.griddynamics.jagger.webclient.client.components.control.model;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/26/13
 */
public class TestInfoLeaf extends SimpleNode {

    public TestInfoLeaf() {}

    public TestInfoLeaf(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }
    @Override
    public List<? extends SimpleNode> getChildren() {
        return Collections.EMPTY_LIST;
    }
}
