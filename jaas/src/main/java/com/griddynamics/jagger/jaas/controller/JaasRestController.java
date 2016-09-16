package com.griddynamics.jagger.jaas.controller;




import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricSummaryValueEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricPlotPointEntity;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author asokol
 *         created 9/14/16
 */
interface JaasRestController {

    @ApiOperation(value = "getSession",
            notes = "Returns session by session id",
            response = SessionEntity.class)
    @ApiImplicitParams(
            @ApiImplicitParam(name = "sessionId", value = "Input session id", required = true, paramType = "path",
                    dataType = "string")
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SessionEntity.class),
    })
    SessionEntity getSession(String sessionId);

    @ApiOperation(value = "getSessions",
            notes = "Returns sessions by session ids",
            response = SessionEntity.class,
            responseContainer = "Set")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", value = "Input session ids", allowMultiple = true, dataType = "string")
    )
    Set<SessionEntity> getSessions(String[] sessionIds);

    @ApiOperation(value = "getTest",
            notes = "Returns test by session id and test name",
            response = TestEntity.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", value = "Input session id", required = true, paramType = "path",
                    dataType = "string"),
            @ApiImplicitParam(name = "testName", value = "Input test name", required = true, paramType = "path",
                    dataType = "string")
    })
    TestEntity getTest(String sessionId, String testName);

    @ApiOperation(value = "getTests",
            notes = "Returns tests by session id",
            response = TestEntity.class,
            responseContainer = "Set")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "sessionId", value = "Input session id", required = true, paramType = "path",
                    dataType = "string")
    )
    Set<TestEntity> getTests(String sessionId);

    @ApiOperation(value = "getMetrics",
            notes = "Returns metrics by test id",
            response = MetricEntity.class,
            responseContainer = "Set")
    @ApiImplicitParams(
            @ApiImplicitParam(name = "testId", value = "Input test id", required = true, dataType = "long",
                    paramType = "path")
    )
    Set<MetricEntity> getMetrics(Long testId);

    @ApiOperation(value = "getMetrics",
            notes = "Returns metrics by session id and test name",
            response = MetricEntity.class,
            responseContainer = "Set")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", value = "Input session id", required = true, paramType = "path",
                    dataType = "string"),
            @ApiImplicitParam(name = "testName", value = "Input test name", required = true, paramType = "path",
                    dataType = "string")
    })
    Set<MetricEntity> getMetrics(String sessionId, String testName);

    @ApiOperation(value = "getMetricsSummary",
            notes = "Returns metric summaries by session id and test name",
            response = MetricSummaryValueEntity.class,
            responseContainer = "Map")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", value = "Input session id", required = true, paramType = "path",
                    dataType = "string"),
            @ApiImplicitParam(name = "testName", value = "Input test name", required = true, paramType = "path",
                    dataType = "string")
    })
    Map<MetricEntity, MetricSummaryValueEntity> getMetricsSummary(String sessionId, String testName);

    @ApiOperation(value = "getMetricPlotData",
            notes = "Returns metric plot data by session id and test name",
            response = MetricPlotPointEntity.class,
            responseContainer = "Map")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sessionId", value = "Input session id", required = true, paramType = "path",
                    dataType = "string"),
            @ApiImplicitParam(name = "testName", value = "Input test name", required = true, paramType = "path",
                    dataType = "string")
    })
    Map<MetricEntity, List<MetricPlotPointEntity>> getMetricPlotData(String sessionId, String testName);
}
