package com.griddynamics.jagger.webclient.client.components.control.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/26/13
 */
public abstract class SimpleNode implements Serializable {

    protected String id;
    protected String displayName;

    public SimpleNode() {}

    public SimpleNode (String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public abstract List<? extends SimpleNode> getChildren();
}
