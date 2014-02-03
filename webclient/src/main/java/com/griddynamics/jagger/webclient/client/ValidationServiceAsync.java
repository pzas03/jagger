package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ValidationServiceAsync {
    void validateDatabaseModel(AsyncCallback<Boolean> async);
}
