package com.griddynamics.jagger.webclient.client.callback;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.ListDataProvider;
import com.griddynamics.jagger.webclient.client.WorkloadTaskDetailsTreeViewModel;
import com.griddynamics.jagger.webclient.client.data.TaskPlotNamesAsyncDataProvider;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.List;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/22/12
 */
public class TaskDataDtoListQueryAsyncCallback implements AsyncCallback<List<TaskDataDto>> {

    private Set<String> sessionIds;
    private final ListDataProvider<TaskDataDto> taskDataProvider;
    private final WorkloadTaskDetailsTreeViewModel workloadTaskDetailsTreeViewModel;

    public TaskDataDtoListQueryAsyncCallback(Set<String> sessionIds, ListDataProvider<TaskDataDto> taskDataProvider, WorkloadTaskDetailsTreeViewModel workloadTaskDetailsTreeViewModel) {
        this.sessionIds = sessionIds;
        this.taskDataProvider = taskDataProvider;
        this.workloadTaskDetailsTreeViewModel = workloadTaskDetailsTreeViewModel;
    }

    public void setSessionIds(Set<String> sessionIds) {
        this.sessionIds = sessionIds;
    }

    @Override
    public void onFailure(Throwable caught) {
        Window.alert("Error is occurred during server request processing (Task data fetching)");
    }

    @Override
    public void onSuccess(List<TaskDataDto> result) {
        if (result.isEmpty()) {
            return;
        }

        // Populate task first level tree with server data
        taskDataProvider.getList().clear();
        taskDataProvider.getList().addAll(result);

        // Populate available plots tree level for each task for selected session
        for (TaskDataDto taskDataDto : result) {
            workloadTaskDetailsTreeViewModel.getPlotNameDataProviders().put
                    (taskDataDto, new TaskPlotNamesAsyncDataProvider(taskDataDto, sessionIds));
        }
    }
}
