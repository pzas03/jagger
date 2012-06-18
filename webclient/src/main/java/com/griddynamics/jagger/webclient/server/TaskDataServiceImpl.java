package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.webclient.client.TaskDataService;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

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

        List<TaskDataDto> taskDataDtoList = null;
        try {
            @SuppressWarnings("unchecked")
            List<TaskData> taskDataList = (List<TaskData>) entityManager.createQuery(
                    "select td from TaskData as td where td.sessionId=:sessionId and td.taskId in (select wd.taskId from WorkloadData as wd where wd.sessionId=:sessionId) order by td.number asc")
                    .setParameter("sessionId", sessionId).getResultList();

            if (taskDataList == null) {
                return Collections.emptyList();
            }

            taskDataDtoList = new ArrayList<TaskDataDto>(taskDataList.size());
            for (TaskData taskData : taskDataList) {
                taskDataDtoList.add(new TaskDataDto(taskData.getId(), taskData.getTaskName()));
            }

            log.info("For session {} was loaded {} tasks for {} ms", new Object[]{sessionId, taskDataDtoList.size(), System.currentTimeMillis() - timestamp});
        } catch (Exception e) {
            log.error("Error was occurred during tasks fetching for session "+sessionId, e);
            throw new RuntimeException(e);
        }
        return taskDataDtoList;
    }

    //TODO Fix situation when two tasks with same name are exists
    @Override
    public List<TaskDataDto> getTaskDataForSessions(Set<String> sessionIds) {
        long timestamp = System.currentTimeMillis();

        List<TaskDataDto> taskDataDtoList = null;

        try {
            @SuppressWarnings("unchecked")
            List<Object[]> taskFrequency = (List<Object[]>) entityManager.createQuery
                    ("select td.taskName, count(td.id), td.number from TaskData as td where  td.sessionId in (:sessionIds)" +
                            "and td.taskId in (select wd.taskId from WorkloadData as wd where wd.sessionId in (:sessionIds)) group by td.taskName order by td.number")
                    .setParameter("sessionIds", sessionIds)
                    .getResultList();
            Set<String> taskFrequencyIndex = new HashSet<String>();
            for (Object[] obj : taskFrequency) {
                String taskName = (String) obj[0];
                Long frequency = (Long) obj[1];
                if (frequency >= sessionIds.size()) {
                    taskFrequencyIndex.add(taskName);
                }
            }
            log.debug("Task frequency: {}", taskFrequencyIndex);

            @SuppressWarnings("unchecked")
            List<TaskData> taskDataList = (List<TaskData>) entityManager.createQuery(
                    "select td from TaskData as td where td.sessionId in (:sessionIds) and td.taskId in (select wd.taskId from WorkloadData as wd where wd.sessionId in (:sessionIds))")
                    .setParameter("sessionIds", sessionIds)
                    .getResultList();

            if (taskDataList == null) {
                return Collections.emptyList();
            }

            taskDataDtoList = new ArrayList<TaskDataDto>(taskDataList.size());

            Map<String, Set<Long>> added = new HashMap<String, Set<Long>>();
            for (TaskData taskData : taskDataList) {
                String taskName = taskData.getTaskName();
                if (taskFrequencyIndex.contains(taskName)) {
                    if (added.get(taskName) == null) {
                        added.put(taskName, new HashSet<Long>());
                    }
                    added.get(taskName).add(taskData.getId());
                }
            }

            for (Map.Entry<String, Set<Long>> entry : added.entrySet()) {
                taskDataDtoList.add(new TaskDataDto(entry.getValue(), entry.getKey()));
            }
            log.info("For sessions {} were loaded {} tasks for {} ms", new Object[]{sessionIds, taskDataDtoList.size(), System.currentTimeMillis() - timestamp});
        } catch (Exception e) {
            log.error("Error was occurred during common tasks fetching for sessions " + sessionIds, e);
            throw new RuntimeException(e);
        }

        return taskDataDtoList;
    }
}
