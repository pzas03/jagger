package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.dbapi.dto.PlotIntegratedDto;


public interface DownloadServiceAsync {

    void createPlotCsvFile(PlotIntegratedDto plot, AsyncCallback<String> async);
}
