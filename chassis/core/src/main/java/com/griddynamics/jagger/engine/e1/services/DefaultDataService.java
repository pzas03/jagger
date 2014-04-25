package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.SessionDataDto;
import com.griddynamics.jagger.dbapi.dto.TaskDataDto;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.dbapi.model.RootNode;
import com.griddynamics.jagger.dbapi.model.TestDetailsNode;
import com.griddynamics.jagger.dbapi.model.TestNode;
import com.griddynamics.jagger.dbapi.util.SessionMatchingSetup;
import com.griddynamics.jagger.engine.e1.services.data.service.*;
import com.griddynamics.jagger.dbapi.entity.MetricDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.*;

//??? check all overrides

//??? how to get metric ids for standard metrics and monitoring

//??? will not need HibDaoSupport

public class DefaultDataService extends HibernateDaoSupport implements DataService {
    private static final Logger log = LoggerFactory.getLogger(DefaultDataService.class);

    DatabaseService databaseService;

    public DefaultDataService(NodeContext context) {
        databaseService = context.getService(DatabaseService.class);
    }

    @Override
    public SessionEntity getSession(String sessionId) {
        Set<SessionEntity> sessions = getSessions(new HashSet<String>(Arrays.asList(sessionId)));
        if (sessions.isEmpty()){
            return null;
        }
        return sessions.iterator().next();
    }

    @Override
    public Set<SessionEntity> getSessions(Set<String> sessionIds) {
        if (sessionIds.isEmpty()){
            return Collections.emptySet();
        }

        List<SessionDataDto> sessionDataDtoList = databaseService.getSessionInfoService().getBySessionIds(0,sessionIds.size(),sessionIds);

        if (sessionDataDtoList.isEmpty()) {
            return Collections.emptySet();
        }

        Set<SessionEntity> entities = new HashSet<SessionEntity>(sessionDataDtoList.size());
        for (SessionDataDto sessionDataDto : sessionDataDtoList) {
            SessionEntity sessionEntity = new SessionEntity();
            sessionEntity.setId(sessionDataDto.getSessionId());
            sessionEntity.setStartDate(sessionDataDto.getStartDate());
            sessionEntity.setEndDate(sessionDataDto.getEndDate());
            sessionEntity.setKernels(sessionDataDto.getActiveKernelsCount());
            sessionEntity.setComment(sessionDataDto.getComment());

            entities.add(sessionEntity);
        }

        return entities;
    }

    @Override
    public Set<TestEntity> getTests(SessionEntity session){
        return getTests(session.getId());
    }

    @Override
    public Set<TestEntity> getTests(String sessionId){
        Map<String, Set<TestEntity>> map = getTests(new HashSet<String>(Arrays.asList(sessionId)));

        Set<TestEntity> result = map.get(sessionId);
        if (result != null){
            return result;
        }

        return Collections.emptySet();
    }

    @Override
    public Map<String, Set<TestEntity>> getTests(Set<String> sessionIds){
        return getTestsWithName(sessionIds, null);
    }

    @Override
    public TestEntity getTestByName(SessionEntity session, String testName){
        return getTestByName(session.getId(), testName);
    }

    @Override
    public TestEntity getTestByName(String sessionId, String testName){
        Map<String, TestEntity> map = getTestsByName(new HashSet<String>(Arrays.asList(sessionId)), testName);

        TestEntity result = map.get(sessionId);
        if (result != null){
            return result;
        }

        return null;
    }

    @Override
    public Map<String, TestEntity> getTestsByName(Set<String> sessionIds, String testName){
        Map<String, Set<TestEntity>> tests = getTestsWithName(sessionIds, testName);

        Map<String, TestEntity> result = new HashMap<String, TestEntity>(tests.size());

        for (Map.Entry<String, Set<TestEntity>> entry : tests.entrySet()){
            Set<TestEntity> testEntities = entry.getValue();
            if (!testEntities.isEmpty()){
                result.put(entry.getKey(), testEntities.iterator().next());
            }
        }

        return result;
    }


    //??? pass testName null to ignore
    private Map<String, Set<TestEntity>> getTestsWithName(Set<String> sessionIds, String testName){
        if (sessionIds.isEmpty()){
            return Collections.emptyMap();
        }

        // Get all test results without matching
        SessionMatchingSetup sessionMatchingSetup = new SessionMatchingSetup(false,Collections.<SessionMatchingSetup.MatchBy>emptySet());
        List<TaskDataDto> taskDataDtoList = databaseService.getTaskDataForSessions(sessionIds,sessionMatchingSetup);

        Map<String, Set<TestEntity>> result = new HashMap<String, Set<TestEntity>>();

        for (TaskDataDto taskDataDto : taskDataDtoList) {
            if (taskDataDto.getSessionIds().size() > 1) {
                log.error("TaskDataDto contains data for more that one session. This is unexpected result. {}", taskDataDto);
            }
            else {
                if (((testName != null) && (testName.equals(taskDataDto.getTaskName()))) ||
                        (testName == null)) {
                    TestEntity testEntity = new TestEntity();
                    testEntity.setId(taskDataDto.getId());
                    testEntity.setDescription(taskDataDto.getDescription());
                    testEntity.setName(taskDataDto.getTaskName());


                    //??? missing load and termination strategy

                    //???
                    // may be use test info provider?


                    if (result.containsKey(taskDataDto.getSessionId())){
                        result.get(taskDataDto.getSessionId()).add(testEntity);
                    }else{
                        Set<TestEntity> list = new HashSet<TestEntity>();
                        list.add(testEntity);
                        result.put(taskDataDto.getSessionId(), list);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public Set<MetricEntity> getMetrics(Long testId){
        Map<Long, Set<MetricEntity>> map = getMetricsByTestIds(new HashSet<Long>(Arrays.asList(testId)));

        Set<MetricEntity> result = map.get(testId);
        if (result != null){
            return result;
        }

        return Collections.emptySet();
    }

    @Override
    public Set<MetricEntity> getMetrics(TestEntity test){
        Map<TestEntity, Set<MetricEntity>> map = getMetricsByTests(new HashSet<TestEntity>(Arrays.asList(test)));

        Set<MetricEntity> result = map.get(test);
        if (result != null){
            return result;
        }

        return Collections.emptySet();
    }

    @Override
    public Map<TestEntity, Set<MetricEntity>> getMetricsByTests(Set<TestEntity> tests){
        Map<Long, TestEntity> map = new HashMap<Long, TestEntity>(tests.size());
        Set<Long> ids = new HashSet<Long>(tests.size());

        for (TestEntity test : tests){
            map.put(test.getId(), test);
            ids.add(test.getId());
        }

        Map<Long, Set<MetricEntity>> metrics = getMetricsByTestIds(ids);

        Map<TestEntity, Set<MetricEntity>> result = new HashMap<TestEntity, Set<MetricEntity>>();

        for (Long key : map.keySet()){
            result.put(map.get(key), metrics.get(key));
        }

        return result;
    }

    @Override
    public Map<Long, Set<MetricEntity>> getMetricsByTestIds(Set<Long> testIds){
        if (testIds.isEmpty()){
            return Collections.emptyMap();
        }

        // Get
        List<String> sessionIds = databaseService.getFetchUtil().getSessionIdsByTaskIds(new HashSet<Long>(testIds));

        SessionMatchingSetup sessionMatchingSetup = new SessionMatchingSetup(false,Collections.<SessionMatchingSetup.MatchBy>emptySet());
        RootNode rootNode = databaseService.getControlTreeForSessions(new HashSet<String>(sessionIds),sessionMatchingSetup);

        // Filter
        List<TestNode> summaryNodeTests = rootNode.getSummaryNode().getTests();
        List<TestDetailsNode> detailsNodeTests = rootNode.getDetailsNode().getTests();

        Iterator<TestNode> iteratorTN = summaryNodeTests.iterator();
        while (iteratorTN.hasNext()) {
            Long testId = iteratorTN.next().getTaskDataDto().getId();
            if (!testIds.contains(testId))
               iteratorTN.remove();
            }

        Iterator<TestDetailsNode> iteratorTDN = detailsNodeTests.iterator();
        while (iteratorTDN.hasNext()) {
            Long testId = iteratorTDN.next().getTaskDataDto().getId();
            if (!testIds.contains(testId))
                iteratorTDN.remove();
        }


        //??? should remember who has summary and who has plots!!!

        // Join
        Map<Long,Set<MetricNameDto>> metrics = new HashMap<Long, Set<MetricNameDto>>();

        for (TestNode testNode : summaryNodeTests) {
            Long testId = testNode.getTaskDataDto().getId();

            if (!metrics.containsKey(testId)) {
                metrics.put(testId,new HashSet<MetricNameDto>());
            }

            for (MetricNode metricNode : testNode.getMetrics()) {
                metrics.get(testId).addAll(metricNode.getMetricNameDtoList());
            }
        }

        for (TestDetailsNode testDetailsNode : detailsNodeTests) {
            Long testId = testDetailsNode.getTaskDataDto().getId();

            if (!metrics.containsKey(testId)) {
                metrics.put(testId,new HashSet<MetricNameDto>());
            }

            for (MetricNode metricNode : testDetailsNode.getMetrics()) {
                metrics.get(testId).addAll(metricNode.getMetricNameDtoList());
            }
        }

        // Convert
        Map<Long, Set<MetricEntity>> result = new HashMap<Long, Set<MetricEntity>>();
        for (Long key : metrics.keySet()) {
            result.put(key,new HashSet<MetricEntity>());

            for (MetricNameDto metricNameDto : metrics.get(key)) {
                MetricEntity metricEntity = new MetricEntity();
                metricEntity.setMetricNameDto(metricNameDto);
                result.get(key).add(metricEntity);
            }
        }

        return result;
    }

    //??? get summary and values separately


    @Override
    public List<MetricValueEntity> getMetricValues(Long testId, String metricId){
        Map<String, List<MetricValueEntity>> map = getMetricValuesByIds(testId, Arrays.asList(metricId));

        List<MetricValueEntity> result = map.get(metricId);
        if (result != null){
            return result;
        }

        return Collections.emptyList();
    }

    @Override
    public List<MetricValueEntity> getMetricValues(TestEntity test, MetricEntity metric){
        return getMetricValues(test.getId(), metric);
    }

    @Override
    public List<MetricValueEntity> getMetricValues(Long testId, MetricEntity metric){
        Map<MetricEntity, List<MetricValueEntity>> map = getMetricValues(testId, Arrays.asList(metric));

        List<MetricValueEntity> result = map.get(metric);
        if (result != null){
            return result;
        }

        return Collections.emptyList();
    }

    @Override
    public Map<String, List<MetricValueEntity>> getMetricValuesByIds(Long testId, List<String> metricIds){
        if (metricIds.isEmpty()){
            return Collections.emptyMap();
        }

        MultiMap<String, MetricValueEntity> result = new MultiMap<String,MetricValueEntity>();

        List<MetricDetails> metricValues = getHibernateTemplate().findByNamedParam("select metric from MetricDetails metric " +
                                                                                    "where metric.taskData.id=:testId",
                                                                                    "testId", testId);
        if (metricValues.isEmpty()){
            return Collections.emptyMap();
        }

        for (MetricDetails metricValue : metricValues){
            MetricValueEntity valueEntity = new MetricValueEntity();
            valueEntity.setTimeStamp(metricValue.getTime());
            valueEntity.setValue(metricValue.getValue());

            result.put(metricValue.getMetric(), valueEntity);
        }

        return result.getOrigin();
    }

    @Override
    public Map<MetricEntity, List<MetricValueEntity>> getMetricValues(TestEntity test, List<MetricEntity> metrics){
        return getMetricValues(test.getId(), metrics);
    }

    @Override
    public Map<MetricEntity, List<MetricValueEntity>> getMetricValues(Long testId, List<MetricEntity> metrics){
        List<String> ids = new ArrayList<String>(metrics.size());
        Map<String, MetricEntity> map = new HashMap<String, MetricEntity>(metrics.size());
        for (MetricEntity metricEntity : metrics){
            ids.add(metricEntity.getMetricId());
            map.put(metricEntity.getMetricId(), metricEntity);
        }

        Map<String, List<MetricValueEntity>> metricValues = getMetricValuesByIds(testId, ids);

        Map<MetricEntity, List<MetricValueEntity>> result = new HashMap<MetricEntity, List<MetricValueEntity>>();
        for (String key : map.keySet()){
            result.put(map.get(key), metricValues.get(key));
        }

        return result;
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

    @Override
    public boolean isAvailable() {
        return true;
    }
}
