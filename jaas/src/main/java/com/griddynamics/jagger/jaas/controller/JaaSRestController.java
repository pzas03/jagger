package com.griddynamics.jagger.jaas.controller;

import com.griddynamics.jagger.engine.e1.services.data.service.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author asokol
 *         created 9/14/16
 */
interface JaaSRestController {

    @ApiImplicitParams(
            @ApiImplicitParam(name = "sessionId", value = "Input session id", required = true, paramType = "path",
                    dataType = "string")
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SessionEntity.class),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 500, message = "Failure")
    })
    SessionEntity getSession(String sessionId);

    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "Input session ids", allowMultiple = true, dataType = "string")
    )
    Set<SessionEntity> getSessions(String[] sessionIds);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", value = "Input session id", required = true, paramType = "path",
                    dataType = "string"),
            @ApiImplicitParam(name = "testName", value = "Input test name", required = true, paramType = "path",
                    dataType = "string")
    })
    TestEntity getTest(String sessionId, String testName);

    @ApiImplicitParams(
            @ApiImplicitParam(name = "sessionId", value = "Input session id", required = true, paramType = "path",
                    dataType = "string")
    )
    Set<TestEntity> getTests(String sessionId);

    @ApiImplicitParams(
            @ApiImplicitParam(name = "testId", value = "Input test id", required = true, dataType = "long",
                    paramType = "path")
    )
    Set<MetricEntity> getMetrics(Long testId);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", value = "Input session id", required = true, paramType = "path",
                    dataType = "string"),
            @ApiImplicitParam(name = "testName", value = "Input test name", required = true, paramType = "path",
                    dataType = "string")
    })
    Set<MetricEntity> getMetrics(String sessionId, String testName);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", value = "Input session id", required = true, paramType = "path",
                    dataType = "string"),
            @ApiImplicitParam(name = "testName", value = "Input test name", required = true, paramType = "path",
                    dataType = "string")
    })
    Map<MetricEntity, MetricSummaryValueEntity> getMetricsSummary(String sessionId, String testName);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", value = "Input session id", required = true, paramType = "path",
                    dataType = "string"),
            @ApiImplicitParam(name = "testName", value = "Input test name", required = true, paramType = "path",
                    dataType = "string")
    })
    Map<MetricEntity, List<MetricPlotPointEntity>> getMetricPlotData(String sessionId, String testName);
}
