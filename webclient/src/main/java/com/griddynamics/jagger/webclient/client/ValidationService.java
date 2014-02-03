package com.griddynamics.jagger.webclient.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("rpc/ValidationService")
public interface ValidationService extends RemoteService {

    boolean validateDatabaseModel();

    public static class Async {
        private static final ValidationServiceAsync ourInstance = (ValidationServiceAsync) GWT.create(ValidationService.class);

        public static ValidationServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
