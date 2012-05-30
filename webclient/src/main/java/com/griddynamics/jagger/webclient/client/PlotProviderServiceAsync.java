package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.PointDto;

import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public interface PlotProviderServiceAsync {
    void getPlotListForTask(long taskId, AsyncCallback<List<PlotNameDto>> async);

    void getThroughputData(long taskId, AsyncCallback<List<PointDto>> async);
}
