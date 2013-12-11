package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;
import com.griddynamics.jagger.webclient.client.dto.WorkloadDetailsDto;

import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 * @deprecated another conception of control since jagger 1.2.2-m3
 */
@RemoteServiceRelativePath("rpc/WorkloadService")
@Deprecated
public interface WorkloadService extends RemoteService {

    List<WorkloadDetailsDto> getWorkloadDetailsForSession(List<String> sessionIds) throws RuntimeException;

    public static class Async {
        private static final WorkloadServiceAsync ourInstance = (WorkloadServiceAsync) GWT.create(WorkloadService.class);

        public static WorkloadServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
