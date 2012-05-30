package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
@RemoteServiceRelativePath("TaskDataService")
public interface TaskDataService extends RemoteService {

    List<TaskDataDto> getTaskDataForSession(String sessionId);

    List<TaskDataDto> getTaskDataForSessions(List<String> sessionIds);

    public static class Async {
        private static final TaskDataServiceAsync ourInstance = (TaskDataServiceAsync) GWT.create(TaskDataService.class);

        public static TaskDataServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
