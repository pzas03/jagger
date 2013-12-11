package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.agent.model.DefaultMonitoringParameters;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessLatencyPercentile;
import com.griddynamics.jagger.monitoring.reporting.GroupKey;
import com.griddynamics.jagger.util.Pair;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import com.griddynamics.jagger.webclient.server.plot.CustomMetricPlotDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */
public class CommonDataProviderImpl implements CommonDataProvider {

    private EntityManager entityManager;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private CustomMetricPlotDataProvider customMetricPlotDataProvider;
    private Map<GroupKey, DefaultWorkloadParameters[]> workloadPlotGroups;
    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;

    public Map<GroupKey, DefaultMonitoringParameters[]> getMonitoringPlotGroups() {
        return monitoringPlotGroups;
    }

    public void setMonitoringPlotGroups(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
    }

    public Map<GroupKey, DefaultWorkloadParameters[]> getWorkloadPlotGroups() {
        return workloadPlotGroups;
    }

    public void setWorkloadPlotGroups(Map<GroupKey, DefaultWorkloadParameters[]> workloadPlotGroups) {
        this.workloadPlotGroups = workloadPlotGroups;
    }

    public CustomMetricPlotDataProvider getCustomMetricPlotDataProvider() {
        return customMetricPlotDataProvider;
    }

    public void setCustomMetricPlotDataProvider(CustomMetricPlotDataProvider customMetricPlotDataProvider) {
        this.customMetricPlotDataProvider = customMetricPlotDataProvider;
    }

    private HashMap<String, Pair<String, String>> standardMetrics;

    @Required
    public void setStandardMetrics(HashMap<String, Pair<String, String>> standardMetrics) {
        this.standardMetrics = standardMetrics;
    }

    @Override
    public Set<MetricNameDto> getMetricNames(Set<TaskDataDto> tests) {
        Long time = System.currentTimeMillis();
        HashSet<MetricNameDto> set = new HashSet<MetricNameDto>();
        for (TaskDataDto taskDataDto : tests){
            for (String standardMetricName : standardMetrics.keySet()){
                MetricNameDto metric = new MetricNameDto();
                metric.setName(standardMetricName);
                metric.setTests(taskDataDto);
                set.add(metric);
            }
            set.addAll(getLatencyMetricsNames(taskDataDto));
            set.addAll(getCustomMetricsNames(taskDataDto));
        }
        log.info("For tasks {} was found {} metrics names for {} ms", new Object[]{tests, set.size(), System.currentTimeMillis() - time});
        return set;
    }

    public Set<MetricNameDto> getCustomMetricsNames(TaskDataDto tests){
        Set<MetricNameDto> metrics;

        List<String> metricNames = entityManager.createNativeQuery("select metric.name from DiagnosticResultEntity as metric " +
                "where metric.workloadData_id in " +
                "(select workloadData.id from WorkloadData as workloadData " +
                "inner join (select id, taskId, sessionId from TaskData where id in (:ids)) as taskData on " +
                "workloadData.taskId=taskData.taskId and workloadData.sessionId=taskData.sessionId)")
                .setParameter("ids", tests.getIds()).getResultList();

        List<String> validatorNames = entityManager.createNativeQuery("select metric.validator from ValidationResultEntity as metric " +
                "where metric.workloadData_id in " +
                "(select workloadData.id from WorkloadData as workloadData " +
                "inner join (select id, taskId, sessionId from TaskData where id in (:ids)) as taskData on " +
                "workloadData.taskId=taskData.taskId and workloadData.sessionId=taskData.sessionId)")
                .setParameter("ids", tests.getIds()).getResultList();
        metrics = new HashSet<MetricNameDto>(metricNames.size()+validatorNames.size());

        for (String name : metricNames){
            if (name == null) continue;

            MetricNameDto metric = new MetricNameDto();
            metric.setTests(tests);
            metric.setName(name);

            metrics.add(metric);
        }

        for (String name : validatorNames){
            if (name == null) continue;

            MetricNameDto validator = new MetricNameDto();
            validator.setTests(tests);
            validator.setName(name);

            metrics.add(validator);
        }

        return metrics;
    }

    public Set<MetricNameDto> getLatencyMetricsNames(TaskDataDto tests){
        Set<MetricNameDto> latencyNames;

        List<WorkloadProcessLatencyPercentile> latency = entityManager.createQuery(
                "select s from  WorkloadProcessLatencyPercentile as s where s.workloadProcessDescriptiveStatistics.taskData.id in (:taskIds) " +
                        "group by s.percentileKey " +
                        "having count(s.id)=:size")
                .setParameter("taskIds", tests.getIds())
                .setParameter("size", (long) tests.getIds().size())
                .getResultList();

        latencyNames = new HashSet<MetricNameDto>(latency.size());
        if (!latency.isEmpty()){
            for(WorkloadProcessLatencyPercentile percentile : latency) {
                MetricNameDto dto = new MetricNameDto();
                dto.setName("Latency "+Double.toString(percentile.getPercentileKey())+" %");
                dto.setTests(tests);
                latencyNames.add(dto);
            }
        }
        return latencyNames;
    }



    @Override
    public List<TaskDataDto> getTaskDataForSession(String sessionId) {
        long timestamp = System.currentTimeMillis();

        List<TaskDataDto> taskDataDtoList = null;
        try {
            @SuppressWarnings("unchecked")
            List<Object[]> taskDataList = entityManager.createNativeQuery(
                    "select taskData.id, workloadTaskData.name, workloadTaskData.description, taskData.taskId from " +
                            "( "+
                            "select " +
                            "l.*, s.name, s.description, s.version " +
                            "from "+
                            "(select * from WorkloadTaskData where sessionId=:sessionId) as l "+
                            "left outer join "+
                            "WorkloadDetails as s "+
                            "on l.scenario_id=s.id "+
                            "where " +
                            "l.sessionId =:sessionId"+
                            ") as workloadTaskData " +
                            "inner join " +
                            "(select * from TaskData where sessionId=:sessionId) as taskData "+
                            "on " +
                            "taskData.taskId=workloadTaskData.taskId and " +
                            "taskData.sessionId=workloadTaskData.sessionId ")
                    .setParameter("sessionId", sessionId).getResultList();
            if (taskDataList == null) {
                return Collections.emptyList();
            }

            Collections.sort(taskDataList, new Comparator<Object[]>() {
                @Override
                public int compare(Object[] o1, Object[] o2) {
                    String o1TaskId = (String)o1[3];
                    String o2TaskId = (String)o2[3];

                    Integer o1Id = Integer.parseInt(o1TaskId.substring(5));
                    Integer o2Id = Integer.parseInt(o2TaskId.substring(5));
                    return o1Id.compareTo(o2Id);
                }
            });

            taskDataDtoList = new ArrayList<TaskDataDto>(taskDataList.size());
            for (Object[] taskData : taskDataList) {
                TaskDataDto dto = new TaskDataDto(((BigInteger)taskData[0]).longValue(), (String)taskData[1], (String)taskData[2]);
                taskDataDtoList.add(dto);
            }
            log.info("For session {} was loaded {} tasks for {} ms", new Object[]{sessionId, taskDataList.size(), System.currentTimeMillis() - timestamp});
        } catch (Exception e) {
            log.error("Error was occurred during tasks fetching for session "+sessionId, e);
            throw new RuntimeException(e);
        }
        return taskDataDtoList;
    }

    @Override
    public Set<PlotNameDto> getPlotNames(Set<String> sessionIds, TaskDataDto taskDataDto) {
        Set<PlotNameDto> plotNameDtoSet = new LinkedHashSet<PlotNameDto>();
        try {
            if (isWorkloadStatisticsAvailable(sessionIds, taskDataDto)) {
                for (Map.Entry<GroupKey, DefaultWorkloadParameters[]> monitoringPlot : workloadPlotGroups.entrySet()) {
                    plotNameDtoSet.add(new PlotNameDto(taskDataDto, monitoringPlot.getKey().getUpperName()));
                }
            }

            for (String sessionId : sessionIds) {
                if (isMonitoringStatisticsAvailable(sessionId)) {
                    for (Map.Entry<GroupKey, DefaultMonitoringParameters[]> monitoringPlot : monitoringPlotGroups.entrySet()) {
                        plotNameDtoSet.add(new PlotNameDto(taskDataDto, monitoringPlot.getKey().getUpperName()));
                    }
                }
            }

            List<PlotNameDto> customMetrics = customMetricPlotDataProvider.getPlotNames(taskDataDto);
            plotNameDtoSet.addAll(customMetrics);

            log.debug("For sessions {} are available these plots: {}", sessionIds, plotNameDtoSet);
        } catch (Exception e) {
            log.error("Error was occurred during task scope plots data getting for session IDs " + sessionIds + ", task name " + taskDataDto.getTaskName(), e);
            throw new RuntimeException(e);
        }

        return plotNameDtoSet;
    }

    private boolean isWorkloadStatisticsAvailable(Set<String> sessionIds, TaskDataDto tests) {
        long timestamp = System.currentTimeMillis();
        long workloadStatisticsCount = (Long) entityManager.createQuery("select count(tis.id) from TimeInvocationStatistics as tis where tis.taskData.sessionId in (:sessionIds) and tis.taskData.id in (:tests)")
                .setParameter("tests", tests.getIds())
                .setParameter("sessionIds", sessionIds)
                .getSingleResult();

        if (workloadStatisticsCount < tests.getIds().size()) {
            log.info("For task ID {} workload statistics were not found in DB for {} ms", tests.getTaskName(), System.currentTimeMillis() - timestamp);
            return false;
        }

        return true;
    }

    private boolean isMonitoringStatisticsAvailable(String sessionId) {
        long timestamp = System.currentTimeMillis();
        long monitoringStatisticsCount = (Long) entityManager.createQuery("select count(ms.id) from MonitoringStatistics as ms where ms.sessionId=:sessionId")
                .setParameter("sessionId", sessionId)
                .getSingleResult();

        if (monitoringStatisticsCount == 0) {
            log.info("For session {} monitoring statistics were not found in DB for {} ms", sessionId, System.currentTimeMillis() - timestamp);
            return false;
        }

        return true;
    }


    @Override
    public Set<PlotNameDto> getSessionScopePlotNames(String sessionId) {
       /// Set<String> plotNameDtoSet = null;
        Set<PlotNameDto> plotNameDtoSet = null;
        try {
            if (!isMonitoringStatisticsAvailable(sessionId)) {
                return Collections.emptySet();
            }

          //  plotNameDtoSet = new LinkedHashSet<String>();
            plotNameDtoSet = new LinkedHashSet<PlotNameDto>();

            for (Map.Entry<GroupKey, DefaultMonitoringParameters[]> monitoringPlot : monitoringPlotGroups.entrySet()) {
            //    plotNameDtoSet.add(monitoringPlot.getKey().getUpperName());
                plotNameDtoSet.add(new PlotNameDto(null, monitoringPlot.getKey().getUpperName()));
            }
        } catch (Exception e) {
            log.error("Error was occurred during session scope plots data getting for session ID " + sessionId, e);
            throw new RuntimeException(e);
        }

        return plotNameDtoSet;
    }

    @Override
    public List<TaskDataDto> getTaskDataForSessions(Set<String> sessionIds) {

        long timestamp = System.currentTimeMillis();
        List<Object[]> list = entityManager.createNativeQuery
                (
                        "select taskData.id, commonTests.name, commonTests.description, taskData.taskId , commonTests.clock, commonTests.clockValue, commonTests.termination" +
                                " from "+
                                "( "+
                                "select test.name, test.description, test.version, test.sessionId, test.taskId, test.clock, test.clockValue, test.termination from " +
                                "( "+
                                "select " +
                                "l.*, s.name, s.description, s.version " +
                                "from "+
                                "(select * from WorkloadTaskData where sessionId in (:sessions)) as l "+
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
                                "(select * from WorkloadTaskData where sessionId in (:sessions)) as l "+
                                "left outer join "+
                                "(select * from WorkloadDetails) as s "+
                                "on l.scenario_id=s.id " +
                                ") as t "+
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
                                ") as commonTests "+
                                "left outer join "+
                                "(select * from TaskData where sessionId in (:sessions)) as taskData "+
                                "on "+
                                "commonTests.sessionId=taskData.sessionId and "+
                                "commonTests.taskId=taskData.taskId "
                ).setParameter("sessions", sessionIds)
                .setParameter("sessionCount", (long) sessionIds.size()).getResultList();

        //group tests by description
        HashMap<String, TaskDataDto> map = new HashMap<String, TaskDataDto>(list.size());
        HashMap<String, Integer> mapIds = new HashMap<String, Integer>(list.size());
        for (Object[] testData : list){
            BigInteger id = (BigInteger)testData[0];
            String name = (String) testData[1];
            String description = (String) testData[2];
            String taskId = (String)testData[3];
            String clock = testData[4] + " (" + testData[5] + ")";
            String termination = (String) testData[6];


            int taskIdInt = Integer.parseInt(taskId.substring(5));
            String key = description+name;
            if (map.containsKey(key)){
                map.get(key).getIds().add(id.longValue());

                Integer oldValue = mapIds.get(key);
                mapIds.put(key, (oldValue==null ? 0 : oldValue)+taskIdInt);
            }else{
                TaskDataDto taskDataDto = new TaskDataDto(id.longValue(), name, description);
                taskDataDto.setClock(clock);
                taskDataDto.setTerminationStrategy(termination);
                //merge
                if (map.containsKey(name)){
                    taskDataDto.getIds().addAll(map.get(name).getIds());

                    taskIdInt = taskIdInt + mapIds.get(name);
                }
                map.put(key, taskDataDto);
                mapIds.put(key, taskIdInt);
            }
        }

        if (map.isEmpty()){
            return Collections.EMPTY_LIST;
        }

        PriorityQueue<Object[]> priorityQueue= new PriorityQueue<Object[]>(mapIds.size(), new Comparator<Object[]>() {
            @Override
            public int compare(Object[] o1, Object[] o2) {
                return ((Comparable)o1[0]).compareTo(o2[0]);
            }
        });

        for (String key : map.keySet()){
            TaskDataDto taskDataDto = map.get(key);
            if (taskDataDto.getIds().size() == sessionIds.size()){
                priorityQueue.add(new Object[]{mapIds.get(key), taskDataDto});
            }
        }

        ArrayList<TaskDataDto> result = new ArrayList<TaskDataDto>(priorityQueue.size());
        while (!priorityQueue.isEmpty()){
            result.add((TaskDataDto)priorityQueue.poll()[1]);
        }

        log.info("For sessions {} was loaded {} tasks for {} ms", new Object[]{sessionIds, result.size(), System.currentTimeMillis() - timestamp});
        return result;
    }
}
