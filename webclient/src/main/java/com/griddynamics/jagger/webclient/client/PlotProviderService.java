package com.griddynamics.jagger.webclient.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
@RemoteServiceRelativePath("rpc/PlotProviderService")
public interface PlotProviderService extends RemoteService {

    List<PlotSeriesDto> getPlotData(long taskId, String plotType) throws RuntimeException;

    List<PlotSeriesDto> getPlotData(Set<Long> taskId, String plotType) throws RuntimeException;

    Map<PlotNameDto,List<PlotSeriesDto>> getPlotDatas(Set<PlotNameDto> plots) throws RuntimeException;

    Map<String,List<PlotSeriesDto>> getSessionScopePlotData(String sessionId, Collection<PlotNameDto> plotType) throws RuntimeException;

    public static class Async {
        private static final PlotProviderServiceAsync ourInstance = (PlotProviderServiceAsync) GWT.create(PlotProviderService.class);

        public static PlotProviderServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
