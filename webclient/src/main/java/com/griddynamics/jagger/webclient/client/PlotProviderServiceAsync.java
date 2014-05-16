package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.dbapi.dto.PlotSeriesDto;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.dto.SessionPlotNameDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public interface PlotProviderServiceAsync {

    void getPlotData(Set<MetricNode> plots, AsyncCallback<Map<MetricNode,PlotSeriesDto>> async);
}
