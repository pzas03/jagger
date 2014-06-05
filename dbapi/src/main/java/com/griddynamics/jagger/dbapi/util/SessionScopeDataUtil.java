package com.griddynamics.jagger.dbapi.util;

import com.google.common.collect.*;
import com.griddynamics.jagger.dbapi.fetcher.AbstractMetricPlotFetcher;
import com.griddynamics.jagger.util.Pair;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 6/5/14
 * Time: 6:21 PM
 * To change this template use File | Settings | File Templates.
 */

public class SessionScopeDataUtil {

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public Multimap<String, AbstractMetricPlotFetcher.MetricRawData> getMetricIdRawMap(
            Collection<AbstractMetricPlotFetcher.MetricRawData> allRawDataForProcessing,
            String sessionId) {

        Collection<AbstractMetricPlotFetcher.MetricRawData> allRawData = dataProcess(allRawDataForProcessing, sessionId);

        Multimap<String, AbstractMetricPlotFetcher.MetricRawData> metricIdRawMap = ArrayListMultimap.create();

        for (AbstractMetricPlotFetcher.MetricRawData rawData : allRawData) {
            metricIdRawMap.put(rawData.getMetricId(), rawData);
        }

        return metricIdRawMap;
    }


    private Collection<AbstractMetricPlotFetcher.MetricRawData> dataProcess(Collection<AbstractMetricPlotFetcher.MetricRawData> forProcess, String sessionId) {

        List<AbstractMetricPlotFetcher.MetricRawData> result = Lists.newArrayList();
        Map<Long, Pair<Long, Long>> startEndPairs = taskIdStartAndEndBySessionId(sessionId);
        Map<String, Multimap<Number, AbstractMetricPlotFetcher.MetricRawData>> lines = getLineForSessionScope(forProcess);


        for (String metricId : lines.keySet()) {
            Long lastTimeOfPreviousTask = (long) 0;
            Long timeShift = (long) 0;
            Long finishTimeFirstTask = (long) 0;
            Long startTimeSecondTask = null;
            Long startTimePre = null;
            for (Long taskId : startEndPairs.keySet()) {
                if (lines.get(metricId).containsKey(taskId) && !startEndPairs.get(taskId).getFirst().equals(startTimePre)) {
                    for (AbstractMetricPlotFetcher.MetricRawData metricRawData : lines.get(metricId).get(taskId)) {

                        if (startTimeSecondTask == null)
                            startTimeSecondTask = startEndPairs.get(taskId).getFirst();
                        if (!startTimeSecondTask.equals(startEndPairs.get(taskId).getFirst())) {
                            startTimeSecondTask = startEndPairs.get(taskId).getFirst();
                            timeShift = startTimeSecondTask - finishTimeFirstTask + lastTimeOfPreviousTask;
                        }

                        metricRawData.setTime(metricRawData.getTime() + timeShift);
                        result.add(metricRawData);

                        lastTimeOfPreviousTask = metricRawData.getTime();
                        finishTimeFirstTask = startEndPairs.get(taskId).getSecond();
                    }
                }
                startTimePre = startEndPairs.get(taskId).getFirst();
            }
        }

        return result;
    }

    private Map<String, Multimap<Number, AbstractMetricPlotFetcher.MetricRawData>> getLineForSessionScope(Collection<AbstractMetricPlotFetcher.MetricRawData> forProcess) {
        Map<String, Multimap<Number, AbstractMetricPlotFetcher.MetricRawData>> lines = new HashMap<String, Multimap<Number, AbstractMetricPlotFetcher.MetricRawData>>();

        for (AbstractMetricPlotFetcher.MetricRawData metricRawData : forProcess) {
            String metricId = metricRawData.getMetricId();
            if (lines.get(metricId) == null) {
                Multimap<Number, AbstractMetricPlotFetcher.MetricRawData> multimap = TreeMultimap.create(numberComparator, metricRawDataComparator);
                lines.put(metricId, multimap);
            }
            lines.get(metricId).put(metricRawData.getWorkloadTaskDataId(), metricRawData);
        }
        return lines;
    }


    public Map<Long, Pair<Long, Long>> taskIdStartAndEndBySessionId(String sessionId) {
        Timestamp start;
        Timestamp end;
        Number taskId;

        List<Object[]> listTaskId = entityManager.createNativeQuery("select distinct t1.id,  w.startTime, w.endTime from " +
                " (select t.id, t.taskId, t.sessionId from TaskData t where t.sessionId=:sessionId) t1, WorkloadData w " +
                "where t1.taskId=w.taskId and t1.sessionId=w.sessionId order by w.startTime").setParameter("sessionId", sessionId).getResultList();

        Map<Long, Pair<Long, Long>> result = Maps.newLinkedHashMap();
        for (Object[] objects : listTaskId) {
            taskId = (Number) objects[0];
            start = (Timestamp) objects[1];
            end = (Timestamp) objects[2];
            result.put(taskId.longValue(), Pair.of(start.getTime(), end.getTime()));
        }
        return result;
    }

    private static Comparator<AbstractMetricPlotFetcher.MetricRawData> metricRawDataComparator = new Comparator<AbstractMetricPlotFetcher.MetricRawData>() {
        public int compare(AbstractMetricPlotFetcher.MetricRawData s1, AbstractMetricPlotFetcher.MetricRawData s2) {
            Long sLong = s1.getTime();
            return sLong.compareTo(s2.getTime());
        }
    };
    private static Comparator<Number> numberComparator = new Comparator<Number>() {
        public int compare(Number s1, Number s2) {
            Long sLong = s1.longValue();
            return sLong.compareTo(s2.longValue());
        }
    };


}
