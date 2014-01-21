package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.coordinator.NodeContext;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/10/13
 * Time: 6:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultSessionInfoService implements SessionInfoService {

    private SessionCommentStorage commentStorage;

    public DefaultSessionInfoService(NodeContext context){
        commentStorage = context.getService(SessionCommentStorage.class);
    }

    @Override
    public String getComment() {
        return commentStorage.get();
    }

    @Override
    public void setComment(String comment) {
        commentStorage.set(comment);
    }

    @Override
    public void appendToComment(String st) {
        commentStorage.append(st);
    }

    @Override
    public void createTag(String name, String description) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setTagDescription(String name, String description) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addTag(String name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
