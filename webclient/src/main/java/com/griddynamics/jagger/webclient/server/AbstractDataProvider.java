package com.griddynamics.jagger.webclient.server;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by kgribov on 3/5/14.
 */
public abstract class AbstractDataProvider{

    protected EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    protected Set<MetricNameDto> getMetricNames(List<TaskDataDto> tests){
        Set<Long> taskIds = new HashSet<Long>();
        for (TaskDataDto tdd : tests) {
            taskIds.addAll(tdd.getIds());
        }

        List<Object[]> parents = entityManager.createNativeQuery("select taskdata.id, workloadData.parentId, taskData.sessionId from TaskData taskData inner join " +
                                                                    "WorkloadData workloadData on taskData.taskId = workloadData.taskId " +
                                                                    " and taskData.sessionId = workloadData.sessionId where taskData.id in (:ids);").setParameter("ids", taskIds).getResultList();




        List<Object[]> testGroups = entityManager.createNativeQuery("select task.id, task.taskId, task.sessionId from TaskData task inner join " +
                                                                        "(select taskdata.id, taskData.taskId, workloadData.parentId, taskData.sessionId from TaskData taskData " +
                                                                        "inner join WorkloadData workloadData on  taskData.taskId=workloadData.taskId " +
                                                                        "and taskData.sessionId=workloadData.sessionId  where taskData.id in (:ids)) parents " +
                                                                        "on task.taskId=parents.parentId and task.sessionId=parents.sessionId;").setParameter("ids", taskIds).getResultList();

        Multimap testGroupMap = getTestsInTestGroup(parents, testGroups);

        Set<Long> groupIds = new HashSet<Long>(testGroups.size());
        for (Object[] testGroup : testGroups){
            groupIds.add(((BigInteger) testGroup[0]).longValue());
        }

        List<Object[]> metricDescriptions = getMetricDescriptions(taskIds);
        List<Object[]> groupsMetricDescriptions = getMetricDescriptions(groupIds);

        if (metricDescriptions.isEmpty() && groupsMetricDescriptions.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        Set<MetricNameDto> metrics = new HashSet<MetricNameDto>(metricDescriptions.size()+groupsMetricDescriptions.size());

        for (Object[] mde : metricDescriptions) {
            for (TaskDataDto td : tests) {
                if (td.getIds().contains((Long) mde[2])) {
                    metrics.add(new MetricNameDto(td, (String)mde[0], (String)mde[1]));
                    MetricNameDto metric = new MetricNameDto();
                    metric.setTest(td);
                    metric.setMetricName((String) mde[0]);
                    metric.setMetricDisplayName((String) mde[1]);
                    metrics.add(metric);
                    break;
                }
            }
        }

        for (Object[] mde : groupsMetricDescriptions){
            for (TaskDataDto td : tests){
                Collection<Long> allTestsInGroup = testGroupMap.get(mde[2]);
                if (containsAtLeastOne(td.getIds(), allTestsInGroup)){
                    metrics.add(new MetricNameDto(td, (String)mde[0], (String)mde[1]));
                }
            }
        }

        return metrics;
    }

    protected abstract List<Object[]> getMetricDescriptions(Set<Long> ids);

    protected Multimap getTestsInTestGroup(List<Object[]> parents, List<Object[]> groups){
        Multimap testMap = ArrayListMultimap.create();
        for (Object[] test : parents){
            String key = (String)test[1] + (String)test[2];
            testMap.put(key, ((BigInteger) test[0]).longValue());
        }

        Multimap testsByGroupId = ArrayListMultimap.create();
        for (Object[] group : groups){
            String key = (String)group[1] + (String)group[2];
            testsByGroupId.putAll(((BigInteger) group[0]).longValue(), testMap.get(key));
        }

        return testsByGroupId;
    }

    protected boolean containsAtLeastOne(Collection origin, Collection elements){
        boolean result = false;
        for (Object element : elements){
            if (origin.contains(element)){
                result = true;
                break;
            }
        }

        return result;
    }

}
