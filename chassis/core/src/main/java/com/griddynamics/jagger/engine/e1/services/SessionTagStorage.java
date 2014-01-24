package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.engine.e1.aggregator.session.model.Tags;
import org.apache.commons.collections.map.HashedMap;

import java.util.*;
/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 1/22/14
 * Time: 6:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionTagStorage {
    private Map<String,String> newTags;
    private Set<String> sessionTags;
    private Map<String,String> updateTags;

    public SessionTagStorage() {
        newTags = new HashMap<String,String>();
        updateTags = new HashMap<String,String>();
        sessionTags = new HashSet<String>();
    }

    public void setNewTag(String tagName, String description){
        newTags.put(tagName, description);
    }


    public void setUpdateTag(String tagName, String newDescription){
        updateTags.put(tagName, newDescription);
    }

    public void setSessionTag(String name){
        sessionTags.add(name);
    }

    public Map<String, String> getNewTags() {
        return newTags;
    }

    public Set<String> getSessionTags() {
        return sessionTags;
    }

    public Map<String, String> getUpdateTags(){
        return updateTags;
    }

}

