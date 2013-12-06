package com.griddynamics.jagger.webclient.client.components.control.model;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/26/13
 */
public class SessionInfoLeaf extends SimpleNode {

    public SessionInfoLeaf () {}

    public SessionInfoLeaf (String id, String displayName) {
        setId(id);
        setDisplayName(displayName);
    }

    @Override
    public List<? extends SimpleNode> getChildren() {
        return Collections.EMPTY_LIST;
    }
}
