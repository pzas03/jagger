package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.webclient.client.data.WebClientProperties;

import java.util.List;
import java.util.Map;

public interface CommonDataServiceAsync {

    void getWebClientProperties(AsyncCallback<WebClientProperties> async);

    void getDefaultMonitoringParameters(AsyncCallback<Map<String,List<String>>> async);
}
