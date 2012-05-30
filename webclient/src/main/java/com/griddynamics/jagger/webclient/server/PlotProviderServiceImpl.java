package com.griddynamics.jagger.webclient.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.griddynamics.jagger.webclient.client.PlotProviderService;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public class PlotProviderServiceImpl extends RemoteServiceServlet implements PlotProviderService {
    private static final List<String> plotNames = new ArrayList<String>();

    static {
        plotNames.add("Latency");
        plotNames.add("Throughput");
    }

    @Override
    public List<PlotNameDto> getPlotListForTask(long taskId) {
        List<PlotNameDto> plotNameDtoList = new ArrayList<PlotNameDto>(plotNames.size());
        for (String name : plotNames) {
            plotNameDtoList.add(new PlotNameDto(taskId, name));
        }
        return plotNameDtoList;
    }
}
