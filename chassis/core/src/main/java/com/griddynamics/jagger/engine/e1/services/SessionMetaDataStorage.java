package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.engine.e1.aggregator.session.model.TagEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;



/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 1/25/14
 * Time: 7:36 PM
 * To change this template use File | Settings | File Templates.
 */

public class SessionMetaDataStorage {

    private StringBuilder currentString;
    private Set<String> sessionTags;
    private List<TagEntity> tagsForSaveOrUpdate;


    public SessionMetaDataStorage(String commentDefaultValue){
        currentString = new StringBuilder(commentDefaultValue);
        sessionTags = new HashSet<String>();
        tagsForSaveOrUpdate = new LinkedList<TagEntity>();
    }

    public synchronized void setComment(String comment){
        currentString = new StringBuilder(comment);
    }

    public synchronized void appendToComment(String toComment){
        currentString.append(toComment);
    }

    public synchronized String getComment(){
        return currentString.toString();
    }

    public void addNewOrUpdateTag(TagEntity newTag){
        tagsForSaveOrUpdate.add(newTag);
    }

    public void addSessionTag(String sessionTagName){
        sessionTags.add(sessionTagName);
    }

    public List<TagEntity> getTagsForSaveOrUpdate() {
        return tagsForSaveOrUpdate;
    }

    public Set<String> getSessionTags() {
        return sessionTags;
    }
}
