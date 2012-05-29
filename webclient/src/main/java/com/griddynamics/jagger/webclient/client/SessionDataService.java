package com.griddynamics.jagger.webclient.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.griddynamics.jagger.webclient.client.dto.PagedSessionDataDto;
import com.griddynamics.jagger.webclient.client.dto.SessionDataDto;

import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
@RemoteServiceRelativePath("SessionDataService")
public interface SessionDataService extends RemoteService {

    PagedSessionDataDto getAll(int start, int length);

    public static class Async {
        private static final SessionDataServiceAsync ourInstance = (SessionDataServiceAsync) GWT.create(SessionDataService.class);

        public static SessionDataServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
