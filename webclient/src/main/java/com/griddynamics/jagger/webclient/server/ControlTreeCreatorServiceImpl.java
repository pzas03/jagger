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
    @Deprecated
    public RootNode getControlTreeForSession(String sessionId) throws RuntimeException {

        try {
            RootNode rootNode = new RootNode();
            SummaryNode sn = new SummaryNode(SUMMARY, CONTROL_SUMMARY_TRENDS);
            SessionInfoNode sin = new SessionInfoNode(SESSION_INFO, SESSION_INFO);
            sin.setSessionInfoList(getSessionInfoLeafList());
            sn.setSessionInfo(sin);
            sn.setTests(getSummaryTaskNodeList(sessionId));
            rootNode.setSummary(sn);

            DetailsNode dn = new DetailsNode(CONTROL_METRICS, CONTROL_METRICS);

//            SessionScopePlotsNode sspn = new SessionScopePlotsNode();
//            sspn.setPlots(getSessionScopePlotNames());
//            dn.setSessionScopePlotsNode(sspn);
//
//            dn.setTests(getDetailsTaskNodeList(sessionId));
//
//            rootNode.setDetailsNode(dn);

            return rootNode;
        } catch (Throwable th) {
            log.error("Error while creating Control Tree", th);
            th.printStackTrace();
            throw new RuntimeException(th);
        }
    }

    @Override
    public RootNode getControlTreeForSessions(Set<String> sessionIds) throws RuntimeException {

        try {
            RootNode rootNode = new RootNode();
            SummaryNode sn = new SummaryNode(CONTROL_SUMMARY_TRENDS, CONTROL_SUMMARY_TRENDS);
            SessionInfoNode sin = new SessionInfoNode(SESSION_INFO, SESSION_INFO);
            sin.setSessionInfoList(getSessionInfoLeafList());
            sn.setSessionInfo(sin);
            sn.setTests(getSummaryTaskNodeList(sessionIds));
            rootNode.setSummary(sn);

            DetailsNode dn = new DetailsNode(CONTROL_METRICS, CONTROL_METRICS);

            if (sessionIds.size() == 1) {
                SessionScopePlotsNode sspn = new SessionScopePlotsNode(SESSION_SCOPE_PLOTS, SESSION_SCOPE_PLOTS);
                sspn.setPlots(getSessionScopePlotNames(sessionIds.iterator().next()));
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

            taskDataDtoList.add(testNode);
        }

        return taskDataDtoList;
    }

    private List<SessionPlotNode> getSessionScopePlotNames(String sessionId) {
        List<SessionPlotNode> plotNodes = new ArrayList<SessionPlotNode>();
        Set<PlotNameDto> metricDtos = databaseFetcher.getSessionScopePlotNames(sessionId);
        for (PlotNameDto mnd : metricDtos) {
            SessionPlotNode mn = new SessionPlotNode();
            mn.setId(SESSION_SCOPE_PREFIX + mnd.getPlotName());
            mn.setDisplayName(mnd.getDisplay());
            mn.setPlotNameDto(mnd);
            plotNodes.add(mn);
        }
        return plotNodes;
    }

    private List<TestNode> getSummaryTaskNodeList(String sessionId) {

        List<TestNode> taskDataDtoList = new ArrayList<TestNode>();
        for (final TaskDataDto tdd : databaseFetcher.getTaskDataForSession(sessionId)) {
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

        // first Test Info should be stand alone dto
        return Collections.EMPTY_LIST;
        /*return Arrays.asList(
                new TestInfoLeaf(task.getTaskName() + "clock", "Clock"),
                new TestInfoLeaf(task.getTaskName() + "termination", "Termination")
        );*/
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

    private List<SessionInfoLeaf> getSessionInfoLeafList() {
        return Collections.EMPTY_LIST;
        /*List<SessionInfoLeaf> result = new ArrayList<SessionInfoLeaf>();

            result.add(new SessionInfoLeaf("sumStartTime", "Star Time"));
            result.add(new SessionInfoLeaf("sumEndTime", "End Time"));
            result.add(new SessionInfoLeaf("sumActiveKernels", "Active Kernels"));
            result.add(new SessionInfoLeaf("sumTaskExecuted", "Task Executed"));
            result.add(new SessionInfoLeaf("sumTaskFailed", "Task Failed"));
            result.add(new SessionInfoLeaf("sumComment", "Comment"));

        return result;*/
    }

}
