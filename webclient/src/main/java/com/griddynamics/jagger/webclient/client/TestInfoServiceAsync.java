package com.griddynamics.jagger.webclient.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import com.griddynamics.jagger.webclient.client.dto.TestInfoDto;

import java.util.Collection;
import java.util.Map;

public interface TestInfoServiceAsync {

    void getTestInfo(TaskDataDto taskDataDto, AsyncCallback<Map<String, TestInfoDto>> async);

    void getTestInfos(Collection<TaskDataDto> taskDataDtos, AsyncCallback<Map<TaskDataDto, Map<String, TestInfoDto>>> async);
}
