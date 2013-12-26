package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.ControlTreeCreatorService;
import com.griddynamics.jagger.webclient.client.components.control.model.*;
import com.griddynamics.jagger.webclient.client.data.MetricRankingProvider;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.griddynamics.jagger.webclient.client.mvp.NameTokens.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/26/13
 */
public class ControlTreeCreatorServiceImpl implements ControlTreeCreatorService {

    Logger log = LoggerFactory.getLogger(ControlTreeCreatorServiceImpl.class);

    CommonDataProvider databaseFetcher;

    public void setDatabaseFetcher(CommonDataProvider databaseFetcher) {
        this.databaseFetcher = databaseFetcher;
    }

    @Override
    public RootNode getControlTreeForSession(String sessionId) throws RuntimeException {

        Set<String> dummySet = new HashSet<String>();
        dummySet.add(sessionId);
        return getControlTreeForSessions(dummySet);
    }

    @Override
    public RootNode getControlTreeForSessions(Set<String> sessionIds) throws RuntimeException {

        try {
            RootNode rootNode = new RootNode();
            SummaryNode sn = new SummaryNode(CONTROL_SUMMARY_TRENDS, CONTROL_SUMMARY_TRENDS);
            SessionInfoNode sin = new SessionInfoNode(SESSION_INFO, SESSION_INFO);
            sin.setSessionInfoList(getSessionInfoLeafList(sessionIds));
            sn.setSessionInfo(sin);
            sn.setTests(getSummaryTaskNodeList(sessionIds));
            rootNode.setSummary(sn);

            DetailsNode dn = new DetailsNode(CONTROL_METRICS, CONTROL_METRICS);

            if (sessionIds.size() == 1) {
                SessionScopePlotsNode sspn = new SessionScopePlotsNode(SESSION_SCOPE_PLOTS, SESSION_SCOPE_PLOTS);
                sspn.setPlots(getSessionScopePlotNames(sessionIds));
                if (!sspn.getPlots().isEmpty()) {
                    dn.setSessionScopePlotsNode(sspn);
                }
            }

            dn.setTests(getDetailsTaskNodeList(sessionIds));

            rootNode.setDetailsNode(dn);

            return rootNode;
        } catch (Throwable th) {
            log.error("Error while creating Control Tree", th);
            th.printStackTrace();
            throw new RuntimeException(th);
        }
    }

    private List<TestDetailsNode> getDetailsTaskNodeList(Set<String> sessionIds) {
        List<TestDetailsNode> taskDataDtoList = new ArrayList<TestDetailsNode>();
        for (final TaskDataDto tdd : databaseFetcher.getTaskDataForSessions(sessionIds)) {
            TestDetailsNode testNode = new TestDetailsNode();
            testNode.setId(METRICS_PREFIX + tdd.getTaskName());
            testNode.setTaskDataDto(tdd);

            testNode.setPlots(getPlotNames(sessionIds, tdd));
            testNode.setMonitoringPlots(getMonitoringPlots(sessionIds, tdd));

            taskDataDtoList.add(testNode);
        }

        return taskDataDtoList;
    }

    private List<MonitoringSessionScopePlotNode> getSessionScopePlotNames(Set<String> sessionIds) {
        return databaseFetcher.getMonitoringPlotNodes(sessionIds);
    }

    private List<TestNode> getSummaryTaskNodeList(Set<String> sessionIds) {

        List<TestNode> taskDataDtoList = new ArrayList<TestNode>();
        for (final TaskDataDto tdd : databaseFetcher.getTaskDataForSessions(sessionIds)) {
            TestNode testNode = new TestNode();
            testNode.setId(SUMMARY_PREFIX + tdd.getTaskName());
            testNode.setTaskDataDto(tdd);

            testNode.setMetrics(getMetricNodeList(tdd));

            TestInfoNode tin = new TestInfoNode(tdd.getTaskName() + TEST_INFO, TEST_INFO);
            tin.setTestInfoList(getTestInfoNamesList(tdd));
            testNode.setTestInfo(tin);

            taskDataDtoList.add(testNode);
        }

        return taskDataDtoList;
    }

    private List<TestInfoLeaf> getTestInfoNamesList(TaskDataDto task) {
        return Collections.EMPTY_LIST;
    }

    private List<PlotNode> getPlotNames(Set<String> sessionIds, TaskDataDto tdd) {

        List<PlotNode> result = new ArrayList<PlotNode>();
        for (PlotNameDto mnd : databaseFetcher.getPlotNames(sessionIds, tdd)) {
            PlotNode mn = new PlotNode();
            mn.setId(METRICS_PREFIX + tdd.getTaskName() + mnd.getPlotName());
            mn.setDisplayName(mnd.getDisplay());
            mn.setPlotName(mnd);
            result.add(mn);
        }
        MetricRankingProvider.sortPlotNodes(result);
        return result;
    }

    private List<MonitoringPlotNode> getMonitoringPlots(Set<String> sessionIds, TaskDataDto tdd) {

        return databaseFetcher.getMonitoringPlotNodes(sessionIds, tdd);
    }

    private List<MetricNode> getMetricNodeList(TaskDataDto dto) {
        List<MetricNode> result = new ArrayList<MetricNode>();
        Set<TaskDataDto> dtos = new HashSet<TaskDataDto>(1);
        dtos.add(dto);

        for (MetricNameDto metricName: databaseFetcher.getMetricNames(dtos)) {
            MetricNode mn = new MetricNode();
            mn.setMetricName(metricName);
            mn.setId(SUMMARY_PREFIX + dto.getTaskName() + metricName.getName());
            mn.setDisplayName(metricName.getDisplay());
            result.add(mn);
        }
        MetricRankingProvider.sortMetricNodes(result);
        return result;
    }

    private List<SessionInfoLeaf> getSessionInfoLeafList(Set<String> sessionIds) {
        return Collections.EMPTY_LIST;
    }

}
