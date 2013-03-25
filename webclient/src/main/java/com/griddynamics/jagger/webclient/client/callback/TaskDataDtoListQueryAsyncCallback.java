package com.griddynamics.jagger.webclient.client.callback;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.webclient.client.TaskDataTreeViewModel;
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
    private final CellTable<TaskDataDto> testGrid;

    public TaskDataDtoListQueryAsyncCallback(Set<String> sessionIds, CellTable<TaskDataDto> testGrid) {
        this.sessionIds = sessionIds;
        this.testGrid = testGrid;
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

        testGrid.redraw();
        testGrid.setRowData(result);

//        // Populate available plots tree level for each task for selected session
//        for (TaskDataDto taskDataDto : result) {
//            taskDataTreeViewModel.getPlotNameDataProviders().put
//                    (taskDataDto, new TaskPlotNamesAsyncDataProvider(taskDataDto, sessionIds));
//        }
    }
}
