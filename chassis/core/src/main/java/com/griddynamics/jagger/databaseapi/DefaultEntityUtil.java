package com.griddynamics.jagger.databaseapi;

import com.griddynamics.jagger.databaseapi.entity.MetricEntity;
import com.griddynamics.jagger.databaseapi.entity.MetricValueEntity;
import com.griddynamics.jagger.databaseapi.entity.SessionEntity;
import com.griddynamics.jagger.databaseapi.entity.TestEntity;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.SessionData;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.DiagnosticResultEntity;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadTaskData;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/17/13
 * Time: 3:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultEntityUtil extends HibernateDaoSupport implements EntityUtil{

    private static final Map<String, String> STANDARD_METRICS = new HashMap<String, String>();
    static {
        STANDARD_METRICS.put(JaggerMetric.Throughput, "Throughput");
        STANDARD_METRICS.put(JaggerMetric.AvgLatency, "AvgLatency");
        STANDARD_METRICS.put(JaggerMetric.Failures, "Failures");
        STANDARD_METRICS.put(JaggerMetric.Samples, "Samples");
        STANDARD_METRICS.put(JaggerMetric.StdDevLatency, "StdDevLatency");
        STANDARD_METRICS.put(JaggerMetric.SuccessRate, "SuccessRate");
    }

    @Override
    public SessionEntity getSession(String sessionId) {
        return getSessions(Arrays.asList(sessionId)).iterator().next();
    }

    @Override
    public List<SessionEntity> getSessions(List<String> sessionIds) {
        List<SessionData> sessions = getHibernateTemplate().findByNamedParam("select ses from SessionData as ses where ses.sessionId in (:sessionIds)", "sessionIds", sessionIds);

        List<SessionEntity> entities = new ArrayList<SessionEntity>(sessions.size());

        for (SessionData sessionData : sessions){
            SessionEntity sessionEntity = new SessionEntity();
            sessionEntity.setId(sessionData.getSessionId());
            sessionEntity.setStartTime(sessionData.getStartTime().getTime());
            sessionEntity.setEndTime(sessionData.getEndTime().getTime());
            sessionEntity.setKernels(sessionData.getActiveKernels());
            sessionEntity.setComment(sessionData.getComment());

            entities.add(sessionEntity);
        }

        return entities;
    }

    public List<TestEntity> getTests(String sessionId){
        Map<String, List<TestEntity>> map = getTests(Arrays.asList(sessionId));

        List<TestEntity> result = map.get(sessionId);
        if (result != null){
            return result;
        }

        return Collections.EMPTY_LIST;
    }

    public TestEntity getTestByName(String sessionId, String testName){
        Map<String, TestEntity> map = getTestsByName(Arrays.asList(sessionId), testName);

        TestEntity result = map.get(sessionId);
        if (result != null){
            return result;
        }

        return null;
    }

    public Map<String, TestEntity> getTestsByName(List<String> sessionIds, String testName){
        Map<String, List<TestEntity>> tests = getTests(sessionIds, testName);

        Map<String, TestEntity> result = new HashMap<String, TestEntity>(tests.size());

        for (Map.Entry<String, List<TestEntity>> entry : tests.entrySet()){
            result.put(entry.getKey(), entry.getValue().iterator().next());
        }

        return result;
    }

    public Map<String, List<TestEntity>> getTests(List<String> sessionIds){
        return getTests(sessionIds, "%");
    }

    private Map<String, List<TestEntity>> getTests(List<String> sessionIds, String testName){
        List<WorkloadTaskData> tasks = getHibernateTemplate().findByNamedParam("select task from WorkloadTaskData as task where task.sessionId in (:sessionIds) and task.scenario.name like :name",
                                                                                new String[]{"sessionIds", "name"}, new Object[]{sessionIds, testName});

        Map<String, List<TestEntity>> result = new HashMap<String, List<TestEntity>>();

        for (WorkloadTaskData task : tasks){
            TestEntity entity = new TestEntity();
            entity.setId(task.getId());
            entity.setName(task.getScenario().getName());
            entity.setDescription(task.getScenario().getDescription());
            entity.setLoad(task.getClock());
            entity.setTerminationStrategy(task.getTermination());

            if (result.containsKey(task.getSessionId())){
                result.get(task.getSessionId()).add(entity);
            }else{
                List<TestEntity> list = new ArrayList<TestEntity>();
                list.add(entity);

                result.put(task.getSessionId(), list);
            }
        }

        return result;
    }

    public List<MetricEntity> getMetrics(Long testId){
        Map<Long, List<MetricEntity>> map = getMetrics(Arrays.asList(testId));

        List<MetricEntity> result = map.get(testId);
        if (result != null){
            return result;
        }

        return Collections.EMPTY_LIST;
    }

    public Map<Long, List<MetricEntity>> getMetrics(List<Long> testIds){
        MultiMap<Long, MetricEntity> result = new MultiMap<Long, MetricEntity>();

        //try to find standard metrics
        List<WorkloadTaskData> tasks = getHibernateTemplate().findByNamedParam("select task from WorkloadTaskData as task where task.id in (:ids)","ids", testIds);
        for (WorkloadTaskData task : tasks){
            MetricEntity throughput = new MetricEntity();
            throughput.setMetricId(JaggerMetric.Throughput);
            throughput.setSummaryValue(task.getThroughput().doubleValue());
            throughput.setDisplayName(STANDARD_METRICS.get(JaggerMetric.Throughput));
            result.put(task.getId(), throughput);

            MetricEntity avgLatency = new MetricEntity();
            avgLatency.setMetricId(JaggerMetric.AvgLatency);
            avgLatency.setSummaryValue(task.getAvgLatency().doubleValue());
            avgLatency.setDisplayName(STANDARD_METRICS.get(JaggerMetric.AvgLatency));
            result.put(task.getId(), avgLatency);

            MetricEntity failures = new MetricEntity();
            failures.setMetricId(JaggerMetric.Failures);
            failures.setSummaryValue(task.getFailuresCount().doubleValue());
            failures.setDisplayName(STANDARD_METRICS.get(JaggerMetric.Failures));
            result.put(task.getId(), failures);

            MetricEntity samples = new MetricEntity();
            samples.setMetricId(JaggerMetric.Samples);
            samples.setSummaryValue(task.getSamples().doubleValue());
            samples.setDisplayName(STANDARD_METRICS.get(JaggerMetric.Samples));
            result.put(task.getId(), samples);

            MetricEntity stdDevLatency = new MetricEntity();
            stdDevLatency.setMetricId(JaggerMetric.StdDevLatency);
            stdDevLatency.setSummaryValue(task.getStdDevLatency().doubleValue());
            stdDevLatency.setDisplayName(STANDARD_METRICS.get(JaggerMetric.StdDevLatency));
            result.put(task.getId(), stdDevLatency);

            MetricEntity successRate = new MetricEntity();
            successRate.setMetricId(JaggerMetric.SuccessRate);
            successRate.setSummaryValue(task.getSuccessRate().doubleValue());
            successRate.setDisplayName(STANDARD_METRICS.get(JaggerMetric.SuccessRate));
            result.put(task.getId(), successRate);
        }

        //try to find monitor metrics


        //try to find custom metrics
        List<DiagnosticResultEntity> customMetrics = getHibernateTemplate().findByNamedParam("select metric " +
                                                                                             "from DiagnosticResultEntity as metric " +
                                                                                             "where metric.name=:name " +
                                                                                             "and metric.workloadData.id in (:ids)", "ids", testIds);
        for (DiagnosticResultEntity customMetric : customMetrics){
            MetricEntity metricEntity = new MetricEntity();
            metricEntity.setMetricId(customMetric.getName());
            //need to change to real display name!!
            metricEntity.setDisplayName(customMetric.getName());
            metricEntity.setSummaryValue(customMetric.getTotal());

            result.put(customMetric.getWorkloadData().getId(), metricEntity);
        }

        return result.getOrigin();
    }

    class MultiMap<K,V>{
        private Map<K,List<V>> map;

        public MultiMap(){
            map = new HashMap<K, List<V>>();
        }

        public void put(K key, V value){
            if (map.containsKey(key)){
                map.get(key).add(value);
            }else{
                ArrayList<V> list = new ArrayList<V>();
                list.add(value);

                map.put(key, list);
            }
        }

        public List<V> get(K key){
            return map.get(key);
        }

        public Map<K,List<V>> getOrigin(){
            return map;
        }
    }
}
