package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.dbapi.model.MetricNode;


public interface DownloadServiceAsync {
    void createPlotCsvFile(MetricNode metricNode, AsyncCallback<String> async);
}
