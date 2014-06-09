package com.griddynamics.jagger.dbapi.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import com.griddynamics.jagger.dbapi.dto.TestInfoDto;
import com.griddynamics.jagger.dbapi.fetcher.AbstractMetricPlotFetcher;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 6/5/14
 * Time: 6:21 PM
 * To change this template use File | Settings | File Templates.
 */

public class SessionScopeDataUtil {

    private FetchUtil fetchUtil;

    @Required
    public void setFetchUtil(FetchUtil fetchUtil) {
        this.fetchUtil = fetchUtil;
    }

    public Multimap<String, AbstractMetricPlotFetcher.MetricRawData> getMetricIdRawMap(
            Collection<AbstractMetricPlotFetcher.MetricRawData> allRawDataForProcessing,
            Set<Long> taskIds) {

        Collection<AbstractMetricPlotFetcher.MetricRawData> allRawData = dataProcess(allRawDataForProcessing, taskIds);

        Multimap<String, AbstractMetricPlotFetcher.MetricRawData> metricIdRawMap = ArrayListMultimap.create();

        for (AbstractMetricPlotFetcher.MetricRawData rawData : allRawData) {
            metricIdRawMap.put(rawData.getMetricId(), rawData);
        }

        return metricIdRawMap;
    }


    private Collection<AbstractMetricPlotFetcher.MetricRawData> dataProcess(Collection<AbstractMetricPlotFetcher.MetricRawData> forProcess, Set<Long> taskIds) {

        List<AbstractMetricPlotFetcher.MetricRawData> result = Lists.newArrayList();
        Map<Long, Map<String, TestInfoDto>> testStartEndMap = fetchUtil.getTestInfoByTaskIds(taskIds);
        Map<String, Multimap<Number, AbstractMetricPlotFetcher.MetricRawData>> lines = getLineForSessionScope(forProcess);

        for (String metricId : lines.keySet()) {
            Long lastTimeOfPreviousTask = 0L;
            Long timeShift = 0L;
            Long finishTimeFirstTask = 0L;
            Long startTimeSecondTask = null;
            Long startTimePre = null;
            for (Long taskId : testStartEndMap.keySet()) {
                Long testStartTime = testStartEndMap.get(taskId).values().iterator().next().getStartTime().getTime();
                Long testEndTime = testStartEndMap.get(taskId).values().iterator().next().getEndTime().getTime();

                if (lines.get(metricId).containsKey(taskId) && !testStartTime.equals(startTimePre)) {
                    for (AbstractMetricPlotFetcher.MetricRawData metricRawData : lines.get(metricId).get(taskId)) {

                        if (startTimeSecondTask == null)
                            startTimeSecondTask = testStartTime;
                        if (!startTimeSecondTask.equals(testStartTime)) {
                            startTimeSecondTask = testStartTime;
                            timeShift = startTimeSecondTask - finishTimeFirstTask + lastTimeOfPreviousTask;
                        }

                        metricRawData.setTime(metricRawData.getTime() + timeShift);
                        result.add(metricRawData);

                        lastTimeOfPreviousTask = metricRawData.getTime();
                        finishTimeFirstTask = testEndTime;
                    }
                }
                startTimePre = testStartTime;
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
