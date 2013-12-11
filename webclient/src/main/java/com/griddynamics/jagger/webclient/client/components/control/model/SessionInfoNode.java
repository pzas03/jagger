package com.griddynamics.jagger.webclient.client.components.control.model;

import java.util.List;

/**
 * User: amikryukov
 * Date: 11/26/13
 */
public class SessionInfoNode extends AbstractIdentifyNode {

    List<SessionInfoLeaf> sessionInfoList;

    public SessionInfoNode() {
    }

    public SessionInfoNode(String id, String displayName) {
        super(id, displayName);
    }

    public List<SessionInfoLeaf> getSessionInfoList() {
        return sessionInfoList;
    }

    public void setSessionInfoList(List<SessionInfoLeaf> sessionInfoList) {
        this.sessionInfoList = sessionInfoList;
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        return sessionInfoList;
    }
}
