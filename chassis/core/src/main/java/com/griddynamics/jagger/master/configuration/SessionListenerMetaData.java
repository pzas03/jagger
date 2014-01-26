package com.griddynamics.jagger.master.configuration;

import com.griddynamics.jagger.engine.e1.services.SessionMetaDataStorage;

/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 1/25/14
 * Time: 10:39 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SessionListenerMetaData extends SessionListener {

    public void persistTags(String sessionId, SessionMetaDataStorage metaDataStorage);

}