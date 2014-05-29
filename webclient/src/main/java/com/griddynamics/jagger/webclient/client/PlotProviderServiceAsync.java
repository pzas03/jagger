package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.dbapi.dto.PlotSeriesDto;
import com.griddynamics.jagger.dbapi.model.MetricNode;

import java.util.Map;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public interface PlotProviderServiceAsync {

    void getPlotData(Set<MetricNode> plots, AsyncCallback<Map<MetricNode,PlotSeriesDto>> async);

    void downloadInCsv(MetricNode metricNode, AsyncCallback<String> async);
}
