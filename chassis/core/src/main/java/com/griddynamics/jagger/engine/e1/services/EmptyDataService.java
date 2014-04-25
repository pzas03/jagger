package com.griddynamics.jagger.engine.e1.services;

import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricValueEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;


//??? logging

public class EmptyDataService implements DataService {

    private static Logger log = LoggerFactory.getLogger(EmptyDataService.class);

    private JaggerPlace jaggerPlace;

    public EmptyDataService(JaggerPlace jaggerPlace) {
        this.jaggerPlace = jaggerPlace;
    }


    @Override
    public SessionEntity getSession(String sessionId) {
        return null;
    }

    @Override
    public Set<SessionEntity> getSessions(Set<String> sessionIds) {
        return null;
    }

    @Override
    public Set<TestEntity> getTests(SessionEntity session) {
        return null;
    }

    @Override
    public Set<TestEntity> getTests(String sessionId) {
        return null;
    }

    @Override
    public Map<String, Set<TestEntity>> getTests(Set<String> sessionIds) {
        return null;
    }

    @Override
    public TestEntity getTestByName(String sessionId, String testName) {
        return null;
    }

    @Override
    public TestEntity getTestByName(SessionEntity session, String testName) {
        return null;
    }

    @Override
    public Map<String, TestEntity> getTestsByName(Set<String> sessionIds, String testName) {
        return null;
    }

    @Override
    public Set<MetricEntity> getMetrics(Long testId) {
        return null;
    }

    @Override
    public Set<MetricEntity> getMetrics(TestEntity test) {
        return null;
    }

    @Override
    public Map<TestEntity, Set<MetricEntity>> getMetricsByTests(Set<TestEntity> tests) {
        return null;
    }

    @Override
    public Map<Long, Set<MetricEntity>> getMetricsByTestIds(Set<Long> testIds) {
        return null;
    }

    @Override
    public List<MetricValueEntity> getMetricValues(Long testId, String metricId) {
        return null;
    }

    @Override
    public List<MetricValueEntity> getMetricValues(Long testId, MetricEntity metric) {
        return null;
    }

    @Override
    public List<MetricValueEntity> getMetricValues(TestEntity test, MetricEntity metric) {
        return null;
    }

    @Override
    public Map<String, List<MetricValueEntity>> getMetricValuesByIds(Long testId, List<String> metricIds) {
        return null;
    }

    @Override
    public Map<MetricEntity, List<MetricValueEntity>> getMetricValues(TestEntity test, List<MetricEntity> metrics) {
        return null;
    }

    @Override
    public Map<MetricEntity, List<MetricValueEntity>> getMetricValues(Long testId, List<MetricEntity> metrics) {
        return null;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }
}
