package com.griddynamics.jagger.databaseapi;

import com.griddynamics.jagger.databaseapi.entity.MetricEntity;
import com.griddynamics.jagger.databaseapi.entity.MetricValueEntity;
import com.griddynamics.jagger.databaseapi.entity.SessionEntity;
import com.griddynamics.jagger.databaseapi.entity.TestEntity;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/6/13
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface EntityUtil {

    SessionEntity getSession(String sessionId);
    List<SessionEntity> getSessions(List<String> sessionIds);

    List<TestEntity> getTests(String sessionId);
    TestEntity getTestByName(String sessionId, String testName);
    Map<String, TestEntity> getTestsByName(List<String> sessionIds, String testName);
    Map<String, List<TestEntity>> getTests(List<String> sessionIds);



//    TestEntity getTestBySessionId(String sessionId, String testName);
//    TestEntity getTest(SessionEntity session, String testName);
//
//    List<TestEntity> getTestsBySessionId(String sessionId);
//    List<TestEntity> getTests(SessionEntity session);
//
//    List<Map<SessionEntity, List<TestEntity>>> getTests(List<SessionEntity> sessions);
//    List<Map<String, List<TestEntity>>> getTestsBySessionsIds(List<String> sessionIds);
//
//    Map<String,TestEntity> getTestsBySessionsIds(List<String> sessionIds, String testName);
//
//    List<MetricEntity> getMetricsByTestId(String testId);
//    List<MetricEntity> getMetrics(TestEntity test);
//
//    List<MetricEntity> getMetrics(String sessionId, String testName);
//
//    List<Map<TestEntity, List<MetricEntity>>> getMetrics(List<TestEntity> tests);
//    List<Map<String, List<MetricEntity>>> getMetricsByTestsIds(List<String> testsIds);
//
//    List<MetricValueEntity> getMetricValuesByTestId(String testId, String metricId);
//    List<MetricValueEntity> getMetricValues(TestEntity test, String metricId);
//    List<MetricValueEntity> getMetricValues(String sessionId, String testName, String metricId);
}
