package com.griddynamics.jagger.webclient.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.griddynamics.jagger.webclient.client.components.control.model.RootNode;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.List;
import java.util.Set;

public interface ControlTreeCreatorServiceAsync {
    void getControlTreeForSession(String sessionId, AsyncCallback<RootNode> async);

    void getControlTreeForSessions(Set<String> sessionIds, AsyncCallback<RootNode> async) throws RuntimeException;
}
