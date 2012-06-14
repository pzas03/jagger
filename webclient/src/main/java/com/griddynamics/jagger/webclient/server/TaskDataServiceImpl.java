package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.webclient.client.TaskDataService;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/30/12
 */
public class TaskDataServiceImpl /*extends RemoteServiceServlet*/ implements TaskDataService {
    private static final Logger log = LoggerFactory.getLogger(TaskDataServiceImpl.class);

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<TaskDataDto> getTaskDataForSession(String sessionId) {
        long timestamp = System.currentTimeMillis();

        List<TaskDataDto> taskDataDtoList;

        @SuppressWarnings("unchecked")
        List<TaskData> taskDataList = (List<TaskData>) entityManager.createQuery(
                "select td from TaskData as td where td.sessionId=:sessionId and td.taskId in (select wd.taskId from WorkloadData as wd where wd.sessionId=:sessionId) order by td.number asc")
                .setParameter("sessionId", sessionId).getResultList();

        if (taskDataList == null) {
            return Collections.emptyList();
        }

        taskDataDtoList = new ArrayList<TaskDataDto>(taskDataList.size());
        for (TaskData taskData : taskDataList) {
            taskDataDtoList.add(new TaskDataDto(taskData.getId(), taskData.getTaskId(), taskData.getSessionId(), taskData.getTaskName(), taskData.getStatus().name()));
        }

        log.info("For session {} was loaded {} tasks for {} ms", new Object[]{sessionId, taskDataDtoList.size(), System.currentTimeMillis() - timestamp});
        return taskDataDtoList;
    }

    @Override
    public List<TaskDataDto> getTaskDataForSessions(List<String> sessionIds) {
        List<TaskDataDto> taskDataDtoList;

        @SuppressWarnings("unchecked")
        List<TaskData> taskDataList = (List<TaskData>) entityManager.createQuery(
                "select td from TaskData as td where td.sessionId in (:sessionIds)").setParameter("sessionIds", sessionIds).getResultList();

        if (taskDataList == null) {
            return Collections.emptyList();
        }

        taskDataDtoList = new ArrayList<TaskDataDto>(taskDataList.size());
        for (TaskData taskData : taskDataList) {
            taskDataDtoList.add(new TaskDataDto(taskData.getId(), taskData.getTaskId(), taskData.getSessionId(), taskData.getTaskName(), taskData.getStatus().name()));
        }

        return taskDataDtoList;
    }
}
