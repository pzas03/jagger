package com.griddynamics.jagger.engine.e1.services;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/11/13
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionCommentStorage {

    private StringBuilder currentString;

    public SessionCommentStorage(){
        currentString = new StringBuilder();
    }

    public SessionCommentStorage(String defaultValue){
        currentString = new StringBuilder(defaultValue);
    }

    public synchronized void setComment(String comment){
        currentString = new StringBuilder(comment);
    }

    public synchronized void append(String toComment){
        currentString.append(toComment);
    }

    public synchronized String getComment(){
        return currentString.toString();
    }
}
