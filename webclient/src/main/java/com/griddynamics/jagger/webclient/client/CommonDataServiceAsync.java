package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.webclient.client.data.WebClientProperties;

public interface CommonDataServiceAsync {

    void getWebClientProperties(AsyncCallback<WebClientProperties> async);
}
