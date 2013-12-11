package com.griddynamics.jagger.webclient.client.components.control.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/26/13
 */
public abstract class AbstractIdentifyNode implements Serializable {

    /**
     * id in tree - uniq for all nodes
     */
    protected String id;

    /**
     * representation of the node in control tree
     */
    protected String displayName;

    public AbstractIdentifyNode() {}

    public AbstractIdentifyNode(String id, String displayName) {
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

    /**
     * @return List of children. empty list if has no children.
     */
    public abstract List<? extends AbstractIdentifyNode> getChildren();
}
