package com.griddynamics.jagger.webclient.client.components.control.model;

import java.util.Collections;
import java.util.List;

/**
 * User: amikryukov
 * Date: 11/26/13
 */
public class SessionInfoLeaf extends AbstractIdentifyNode {

    public SessionInfoLeaf () {}

    public SessionInfoLeaf (String id, String displayName) {
        setId(id);
        setDisplayName(displayName);
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        return Collections.EMPTY_LIST;
    }
}
