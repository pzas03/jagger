package com.griddynamics.jagger.jaas.controller;

import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricSummaryValueEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricPlotPointEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JaaS REST API controller based on Spring MVC.
 */
@RequestMapping(value = "/jaas")
@RestController
public class JaasRestControllerImp implements JaasRestController {

    private final static Logger LOGGER = LoggerFactory.getLogger(JaasRestControllerImp.class);

    @Autowired
    private DataService dataService;

    @GetMapping(value = "/sessions/{sessionId}")
    public SessionEntity getSession(@PathVariable String sessionId) {
        LOGGER.debug("Input session id: {}", sessionId);
        return dataService.getSession(sessionId);
    }

    @GetMapping(value = "/sessions")
    public Set<SessionEntity> getSessions(@RequestParam(name = "id", required = false) String[] sessionIds) {
        LOGGER.debug("Input session ids: {}", sessionIds);
        if (sessionIds == null) {
            sessionIds = new String[0];
        }
        return dataService.getSessions(Arrays.asList(sessionIds));
    }

    @GetMapping(value = "/sessions/{sessionId}/tests/{testName}")
    public TestEntity getTest(@PathVariable String sessionId, @PathVariable String testName) {
        LOGGER.debug("Input session id: {}", sessionId);
        LOGGER.debug("Input test name: {}", testName);
        return dataService.getTestByName(sessionId, testName);
    }

    @GetMapping(value = "/sessions/{sessionId}/tests")
    public Set<TestEntity> getTests(@PathVariable String sessionId) {
        LOGGER.debug("Input session id: {}", sessionId);
        return dataService.getTests(sessionId);
    }

    @GetMapping(value = "/tests/{testId}/metrics")
    public Set<MetricEntity> getMetrics(@PathVariable Long testId) {
        LOGGER.debug("Input test id: {}", testId);
        return dataService.getMetrics(testId);
    }

    @GetMapping(value = "/sessions/{sessionId}/tests/{testName}/metrics")
    public Set<MetricEntity> getMetrics(@PathVariable String sessionId, @PathVariable String testName) {
        LOGGER.debug("Input session id: {}", sessionId);
        LOGGER.debug("Input test name: {}", testName);

        TestEntity testEntity = getTest(sessionId, testName);
        return dataService.getMetrics(testEntity);
    }

    @GetMapping(value = "/sessions/{sessionId}/tests/{testName}/metrics/summary")
    public Map<MetricEntity, MetricSummaryValueEntity> getMetricsSummary(@PathVariable String sessionId,
                                                                         @PathVariable String testName
    ) {
        LOGGER.debug("Input session id: {}", sessionId);
        LOGGER.debug("Input test name: {}", testName);

        Set<MetricEntity> metricEntities = getMetrics(sessionId, testName);
        return dataService.getMetricSummary(metricEntities);
    }

    @GetMapping(value = "/sessions/{sessionId}/tests/{testName}/metrics/plot-data")
    public Map<MetricEntity, List<MetricPlotPointEntity>> getMetricPlotData(@PathVariable String sessionId,
                                                                            @PathVariable String testName
    ) {
        LOGGER.debug("Input session id: {}", sessionId);
        LOGGER.debug("Input test name: {}", testName);

        Set<MetricEntity> metricEntities = getMetrics(sessionId, testName);
        return dataService.getMetricPlotData(metricEntities);
    }
}
