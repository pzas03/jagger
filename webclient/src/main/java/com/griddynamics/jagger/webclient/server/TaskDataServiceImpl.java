package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadTaskData;
import com.griddynamics.jagger.webclient.client.TaskDataService;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
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
            List<WorkloadTaskData> taskDataList = (List<WorkloadTaskData>) entityManager.createQuery(
                    "select td from WorkloadTaskData as td where td.sessionId=:sessionId and td.taskId in (select wd.taskId from WorkloadData as wd where wd.sessionId=:sessionId) order by td.number asc")
                    .setParameter("sessionId", sessionId).getResultList();
            if (taskDataList == null) {
                return Collections.emptyList();
            }

            taskDataDtoList = new ArrayList<TaskDataDto>(taskDataList.size());
            for (WorkloadTaskData taskData : taskDataList) {
                taskDataDtoList.add(new TaskDataDto(taskData.getId(), taskData.getScenario().getName(), taskData.getScenario().getVersion()));
            }

            log.info("For session {} was loaded {} tasks for {} ms", new Object[]{sessionId, taskDataDtoList.size(), System.currentTimeMillis() - timestamp});
        } catch (Exception e) {
            log.error("Error was occurred during tasks fetching for session "+sessionId, e);
            throw new RuntimeException(e);
        }
        return taskDataDtoList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TaskDataDto> getTaskDataForSessions(Set<String> sessionIds) {

        List<TaskDataDto> taskDataDtoList = new ArrayList<TaskDataDto>();

        List<Object[]> list = entityManager.createNativeQuery
               ("select test.id, test.name, test.description, test.version from " +
                                                        "( "+
                                                              "select " +
                                                                    "l.*, s.name, s.description, s.version " +
                                                              "from "+
                                                                     "WorkloadTaskData as l "+
                                                              "left outer join "+
                                                                     "(select * from WorkloadDetails) as s "+
                                                              "on l.scenario_id=s.id "+
                                                         ") as test " +
               "inner join " +
                       "( " +
                            "select t.* from "+
                                        "( "+
                                           "select " +
                                                "l.*, s.name, s.description, s.version " +
                                           "from "+
                                                "WorkloadTaskData as l "+
                                           "left outer join "+
                                                "(select * from WorkloadDetails) as s "+
                                           "on l.scenario_id=s.id "+
                                        ") as t "+
                            "where "+
                                "sessionId in (:sessions) "+
                            "group by "+
                                "t.termination, t.clock, t.clockValue, t.name, t.version "+
                            "having count(t.id)>=:sessionCount" +

                       ") as testArch " +
               "on "+
                       "test.clock=testArch.clock and "+
                       "test.clockValue=testArch.clockValue and "+
                       "test.termination=testArch.termination and "+
                       "test.name=testArch.name and "+
                       "test.version=testArch.version "+
               "where test.sessionId in(:sessions)"+
               "order by test.description"
               ).setParameter("sessions", sessionIds)
                .setParameter("sessionCount", (long) sessionIds.size()).getResultList();

        //group tests by description
        HashMap<String, HashMap<String, TaskDataDto>> map = new HashMap<String, HashMap<String, TaskDataDto>>();
        for (Object[] testFields : list){
            BigInteger id = (BigInteger)testFields[0];
            String name = (String) testFields[1];
            String description = (String) testFields[2];
            String version = (String) testFields[3];
            if (map.containsKey(name)){
                HashMap<String, TaskDataDto> descriptionMap = map.get(name);
                if (description.equals("")){
                    for (String descriptionKey : descriptionMap.keySet()){
                        if (!descriptionKey.equals("")){
                            descriptionMap.get(descriptionKey).getIds().add(id.longValue());
                        }
                    }
                    if (descriptionMap.containsKey("")){
                        descriptionMap.get("").getIds().add(id.longValue());
                    }else{
                        descriptionMap.put("", new TaskDataDto(id.longValue(), name, version));
                    }
                }else{
                    if (descriptionMap.containsKey(description)){
                        TaskDataDto test = descriptionMap.get(description);
                        test.getIds().add(id.longValue());
                    }else{
                        TaskDataDto test = new TaskDataDto(id.longValue(), name, version);
                        descriptionMap.put(description, test);
                    }
                }
            }else{
                HashMap<String, TaskDataDto> descriptionMap = new HashMap<String, TaskDataDto>();
                descriptionMap.put(description, new TaskDataDto(id.longValue(), name, version));
                map.put(name, descriptionMap);
            }
        }
        for (String testName : map.keySet()){
            HashMap<String, TaskDataDto> allDescriptions = map.get(testName);
            for (String description : allDescriptions.keySet()){
                TaskDataDto test = allDescriptions.get(description);
                if (test.getIds().size() >= sessionIds.size()){
                    taskDataDtoList.add(test);
                }
            }
        }
        return taskDataDtoList;
    }
}
