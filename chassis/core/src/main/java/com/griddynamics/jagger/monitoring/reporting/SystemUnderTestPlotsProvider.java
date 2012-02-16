/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.monitoring.reporting;

import com.google.common.collect.*;
import com.griddynamics.jagger.agent.model.*;
import com.griddynamics.jagger.monitoring.MonitoringParameterBean;
import com.griddynamics.jagger.monitoring.model.MonitoringStatistics;
import com.griddynamics.jagger.reporting.MappedReportProvider;
import com.griddynamics.jagger.reporting.ReportingContext;
import com.griddynamics.jagger.reporting.chart.ChartHelper;
import com.griddynamics.jagger.util.Pair;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * @author Alexey Kiselyov
 *         Date: 26.07.11
 */
public class SystemUnderTestPlotsProvider extends AbstractMonitoringReportProvider implements MappedReportProvider<String> {
    private Logger log = LoggerFactory.getLogger(SystemUnderTestPlotsProvider.class);

    private static final int PLOT_HEIGHT = 400;
    private static final int PLOT_WIDTH = 1200;

    private Map<GroupKey, MonitoringParameter[]> plotGroups;
    private boolean showPlotsBySuT;

    private ReportingContext context;
    private String template;
    private boolean enable;
    private Map<String, List<MonitoringReporterData>> taskPlots;

    @Override
    public void clearCache() {
        super.clearCache();

        taskPlots = null;
    }

    public JRDataSource getDataSource(String taskId) {
        log.debug("Report for task with id {} requested", taskId);
        if (!this.enable) {
            return new JRBeanCollectionDataSource(Collections.emptySet());
        }

        if (taskPlots == null) {
            log.debug("Initing task plots");
            taskPlots = createTaskPlots();
        }

        loadMonitoringMap();

        log.debug("Loading data for task id {}", taskId);
        List<MonitoringReporterData> data = taskPlots.get(taskId);

        if (data == null) {
            log.debug("Data not found for task {}", taskId);
            String monitoringTaskId = relatedMonitoringTask(taskId);
            log.debug("Related task {}", monitoringTaskId);
            data = taskPlots.get(monitoringTaskId);
        }

        if (data == null) {
            log.warn("SuT plots not found for task with id {}", taskId);
            log.warn("Check that MonitoringAggregator is configured!!!");
        }

        return new JRBeanCollectionDataSource(data);
    }

    public Map<String, List<MonitoringReporterData>> createTaskPlots() {
        Map<String, List<MonitoringReporterData>> taskPlots = Maps.newHashMap();
        final String sessionId = getSessionIdProvider().getSessionId();

        Table<String, MonitoringParameter, List<MonitoringStatistics>> aggregatedByTasks = aggregateGlobalTaskStatistics(sessionId);

        Table<String, String, Map<MonitoringParameter, List<MonitoringStatistics>>> aggregatedBySuT = null;
        if (showPlotsBySuT) {
            aggregatedBySuT = aggregateTaskStatisticsBySuT(sessionId);
        }

        for (String taskId : aggregatedByTasks.rowKeySet()) {

            List<MonitoringReporterData> plots = Lists.newLinkedList();
            for (GroupKey groupName : plotGroups.keySet()) {
                XYSeriesCollection chartsCollection = new XYSeriesCollection();
                for (MonitoringParameter parameterId : plotGroups.get(groupName)) {
                    MonitoringParameterBean param = MonitoringParameterBean.copyOf(parameterId);
                    if (aggregatedByTasks.containsColumn(param)) {
                        XYSeries values = new XYSeries(param.getDescription());
                        for (MonitoringStatistics monitoringStatistics : aggregatedByTasks.get(taskId, param)) {
                            values.add(monitoringStatistics.getTime(), monitoringStatistics.getAverageValue());
                        }
                        if (values.isEmpty()) values.add(0, 0);
                        chartsCollection.addSeries(values);
                    }
                }

                log.debug("group name \n{} \nparams {}]\n", groupName, Lists.newArrayList(plotGroups.get(groupName)));

                Pair<String, XYSeriesCollection> pair = ChartHelper.adjustTime(chartsCollection);

                chartsCollection = pair.getSecond();

                if (chartsCollection.getSeriesCount() > 0) {
                    JFreeChart chart = ChartHelper.createXYChart(null, chartsCollection, "Time (" + pair.getFirst() + ")",
                            groupName.getLeftName(), 3, 2, ChartHelper.ColorTheme.LIGHT);
                    BufferedImage imageValues = ChartHelper.extractImage(chart, PLOT_WIDTH, PLOT_HEIGHT);

                    MonitoringReporterData monitoringReporterData = new MonitoringReporterData();
                    monitoringReporterData.setParameterName(groupName.getUpperName());
                    monitoringReporterData.setTitle(groupName.getUpperName());
                    monitoringReporterData.setPlot(imageValues);
                    plots.add(monitoringReporterData);
                }
            }

            if (aggregatedBySuT != null) {
                Map<String, Map<MonitoringParameter, List<MonitoringStatistics>>> task = aggregatedBySuT.row(taskId);
                for (String sysUnderTestUrl : task.keySet()) {
                    for (GroupKey groupName : plotGroups.keySet()) {
                        XYSeriesCollection chartsCollection = new XYSeriesCollection();
                        for (MonitoringParameter parameterId : plotGroups.get(groupName)) {
                            MonitoringParameterBean param = MonitoringParameterBean.copyOf(parameterId);
                            if (task.get(sysUnderTestUrl).containsKey(param)) {
                                XYSeries values = new XYSeries(param.getDescription());
                                for (MonitoringStatistics monitoringStatisticsBySuT : task.get(sysUnderTestUrl).get(param)) {
                                    values.add(monitoringStatisticsBySuT.getTime(), monitoringStatisticsBySuT.getAverageValue());
                                }
                                if (values.isEmpty()) values.add(0, 0);
                                chartsCollection.addSeries(values);
                            }
                        }

                        Pair<String, XYSeriesCollection> pair = ChartHelper.adjustTime(chartsCollection);

                        chartsCollection = pair.getSecond();


                        if (chartsCollection.getSeriesCount() > 0) {
                            JFreeChart chart = ChartHelper.createXYChart(null,
                                    chartsCollection, "Time (" + pair.getFirst() + ")", groupName.getLeftName(), 3, 2, ChartHelper.ColorTheme.LIGHT);
                            BufferedImage imageValues = ChartHelper.extractImage(chart, PLOT_WIDTH, PLOT_HEIGHT);

                            MonitoringReporterData monitoringReporterData = new MonitoringReporterData();
                            monitoringReporterData.setParameterName(groupName.getUpperName());
                            monitoringReporterData.setTitle(groupName.getUpperName() + " on " + sysUnderTestUrl);
                            monitoringReporterData.setPlot(imageValues);
                            plots.add(monitoringReporterData);
                        }
                    }
                }
            }

            taskPlots.put(taskId, plots);
        }
        return taskPlots;
    }

    private Table<String, MonitoringParameter, List<MonitoringStatistics>> aggregateGlobalTaskStatistics(String sessionId) {

        Table<String, MonitoringParameter, List<MonitoringStatistics>> aggregatedByTasks = HashBasedTable.create();

        @SuppressWarnings("unchecked")
        List<MonitoringStatistics> resultsByAgent = getHibernateTemplate().find(
                "select new MonitoringStatistics(" +
                        "'AVG', 'AVG', ms.sessionId, ms.taskData, ms.time, ms.parameterId, avg(ms.averageValue)" +
                        ") " +
                        "from MonitoringStatistics ms where ms.sessionId=? " +
                        "group by ms.sessionId, ms.time, ms.taskData, ms.parameterId order by ms.time desc", sessionId
        );

        for (MonitoringStatistics sumMonitoringStatistics : resultsByAgent) {
            String taskId = sumMonitoringStatistics.getTaskData().getTaskId();
            MonitoringParameter parameterId = sumMonitoringStatistics.getParameterId();
            List<MonitoringStatistics> monitoringStatisticByAgents = aggregatedByTasks.get(taskId, parameterId);
            if (monitoringStatisticByAgents == null) {
                monitoringStatisticByAgents = Lists.newArrayList();
                aggregatedByTasks.put(taskId, parameterId, monitoringStatisticByAgents);
            }
            monitoringStatisticByAgents.add(sumMonitoringStatistics);
        }

        if (log.isDebugEnabled()) {
            Set<String> params2debug = Sets.newTreeSet();

            for (MonitoringParameter parameter : aggregatedByTasks.columnKeySet()) {
                params2debug.add(parameter.getDescription());
            }

            log.debug("params {}", params2debug);
        }


        return aggregatedByTasks;
    }

    private Table<String, String, Map<MonitoringParameter, List<MonitoringStatistics>>> aggregateTaskStatisticsBySuT(String sessionId) {

        Table<String, String, Map<MonitoringParameter, List<MonitoringStatistics>>> aggregatedBySuT = HashBasedTable.create();

        @SuppressWarnings("unchecked")
        List<MonitoringStatistics> resultsBySuT = getHibernateTemplate().find(
                "from MonitoringStatistics ms where ms.sessionId=?", sessionId
        );
        for (MonitoringStatistics monitoringStatistics : resultsBySuT) {
            if (monitoringStatistics.getParameterId().getLevel() == MonitoringParameterLevel.SUT) {
                String taskId = monitoringStatistics.getTaskData().getTaskId();
                String sysUnderTestUrl = monitoringStatistics.getSystemUnderTestUrl();
                MonitoringParameter parameterId = monitoringStatistics.getParameterId();
                Map<MonitoringParameter, List<MonitoringStatistics>> parameterForSysUnderTestSetMap = aggregatedBySuT.get(taskId, sysUnderTestUrl);
                if (parameterForSysUnderTestSetMap == null) {
                    parameterForSysUnderTestSetMap = Maps.newLinkedHashMap();
                    aggregatedBySuT.put(taskId, sysUnderTestUrl, parameterForSysUnderTestSetMap);
                }
                List<MonitoringStatistics> monitoringStatisticsBySuTs = parameterForSysUnderTestSetMap.get(parameterId);
                if (monitoringStatisticsBySuTs == null) {
                    monitoringStatisticsBySuTs = Lists.newArrayList();
                    parameterForSysUnderTestSetMap.put(parameterId, monitoringStatisticsBySuTs);
                }
                monitoringStatisticsBySuTs.add(monitoringStatistics);
            }
        }

        return aggregatedBySuT;
    }

    @Override
    public JasperReport getReport(String key) {
        return this.context.getReport(template);
    }

    @Override
    public void setContext(ReportingContext context) {
        this.context = context;
    }

    @Required
    public void setTemplate(String template) {
        this.template = template;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Required
    public void setPlotGroups(Map<GroupKey, MonitoringParameter[]> plotGroups) {
        this.plotGroups = plotGroups;
    }

    @Required
    public void setShowPlotsBySuT(boolean showPlotsBySuT) {
        this.showPlotsBySuT = showPlotsBySuT;
    }

    public static class MonitoringReporterData {
        private String parameterName;
        private String title;
        private Image plot;

        public String getParameterName() {
            return this.parameterName;
        }

        public void setParameterName(String parameterName) {
            this.parameterName = parameterName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Image getPlot() {
            return this.plot;
        }

        public void setPlot(Image plot) {
            this.plot = plot;
        }
    }
}
