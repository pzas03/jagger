package com.griddynamics.jagger.webclient.server.plot;

import com.griddynamics.jagger.agent.model.DefaultMonitoringParameters;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadData;
import com.griddynamics.jagger.monitoring.model.MonitoringStatistics;
import com.griddynamics.jagger.monitoring.reporting.GroupKey;
import com.griddynamics.jagger.webclient.client.dto.PlotDatasetDto;
import com.griddynamics.jagger.webclient.client.dto.PlotSeriesDto;
import com.griddynamics.jagger.webclient.client.dto.PointDto;
import com.griddynamics.jagger.webclient.server.ColorCodeGenerator;
import com.griddynamics.jagger.webclient.server.DataProcessingUtil;
import com.griddynamics.jagger.webclient.server.EntityManagerProvider;
import com.griddynamics.jagger.webclient.server.LegendProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.util.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/5/12
 */
public class MonitoringPlotDataProvider implements PlotDataProvider {
    private static final Logger log = LoggerFactory.getLogger(MonitoringPlotDataProvider.class);

    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;
    private final LegendProvider legendProvider = new LegendProvider();

    public MonitoringPlotDataProvider(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
    }

    @Override
    public PlotSeriesDto getPlotData(long taskId, String plotName) {
        EntityManager entityManager = EntityManagerProvider.getEntityManagerFactory().createEntityManager();

        PlotSeriesDto plotSeriesDto;
        try {
            GroupKey groupKey = new GroupKey(plotName);
            DefaultMonitoringParameters[] defaultMonitoringParametersGroup = monitoringPlotGroups.get(groupKey);
            log.debug("For plot {} there are exist {} monitoring parameters", plotName, defaultMonitoringParametersGroup);

            TaskData workloadTaskData = entityManager.find(TaskData.class, taskId);

            WorkloadData workloadData = (WorkloadData) entityManager
                    .createQuery("select wd from WorkloadData as wd where wd.sessionId=:sessionId and wd.taskId=:taskId")
                    .setParameter("sessionId", workloadTaskData.getSessionId())
                    .setParameter("taskId", workloadTaskData.getTaskId())
                    .getSingleResult();

            TaskData monitoringTaskData = (TaskData) entityManager.createQuery(
                    "select td from TaskData as td where td.sessionId=:sessionId and td.taskId=" +
                    "(select pm.monitoringId from PerformedMonitoring as pm where pm.sessionId=:sessionId and pm.parentId=:parentId)")
                    .setParameter("sessionId", workloadData.getSessionId())
                    .setParameter("parentId", workloadData.getParentId())
                    .getSingleResult();


            List<String> monitoringParametersList = new ArrayList<String>();
            for (DefaultMonitoringParameters defaultMonitoringParameter : defaultMonitoringParametersGroup) {
                monitoringParametersList.add(defaultMonitoringParameter.getDescription());
            }

            List<MonitoringStatistics> monitoringStatisticsList = (List<MonitoringStatistics>) entityManager.createQuery(
                    "select ms from MonitoringStatistics as ms where ms.taskData = :monitoringTaskData " +
                            "and ms.parameterId.description in (:descrList)")
                    .setParameter("monitoringTaskData", monitoringTaskData)
                    .setParameter("descrList", monitoringParametersList)
                    .getResultList();

            log.debug("monitoringStatisticsList {}", monitoringStatisticsList);

            List<PlotDatasetDto> plotDatasetDtoList = new ArrayList<PlotDatasetDto>();
            for (Map.Entry<String, Map<String, List<MonitoringStatistics>>> entry: composeByDescriptionAndBoxIdentifier(monitoringStatisticsList).entrySet()) {
                String description = entry.getKey();
                for (Map.Entry<String, List<MonitoringStatistics>> boxEntry : entry.getValue().entrySet()) {
                    String boxIdentifier = boxEntry.getKey();

                    List<PointDto> pointDtoList = new ArrayList<PointDto>();
                    for (MonitoringStatistics monitoringStatistics : boxEntry.getValue()) {
                        pointDtoList.add(new PointDto(DataProcessingUtil.round(monitoringStatistics.getTime()/1000.0D), DataProcessingUtil.round(monitoringStatistics.getAverageValue())));
                    }
                    PlotDatasetDto plotDatasetDto = new PlotDatasetDto(pointDtoList, description+boxIdentifier, ColorCodeGenerator.getHexColorCode());
                    plotDatasetDtoList.add(plotDatasetDto);
                }
            }
            plotSeriesDto = new PlotSeriesDto(plotDatasetDtoList, "Time, sec", "", legendProvider.getPlotHeader(monitoringTaskData.getId(), plotName));
        } finally {
            entityManager.close();
        }

        return plotSeriesDto;
    }

    private Map<String, Map<String, List<MonitoringStatistics>>> composeByDescriptionAndBoxIdentifier(List<MonitoringStatistics> monitoringStatisticsList) {
        Map<String, Map<String, List<MonitoringStatistics>>> map = new HashMap<String, Map<String, List<MonitoringStatistics>>>();

        for (MonitoringStatistics monitoringStatistics : monitoringStatisticsList) {
            String description = monitoringStatistics.getParameterId().getDescription();
            String boxIdentifier = monitoringStatistics.getBoxIdentifier();

            if (!map.containsKey(description)) {
                map.put(description, new HashMap<String, List<MonitoringStatistics>>());
            }
            Map<String, List<MonitoringStatistics>> boxIdentifiersMap = map.get(description);
            if (!boxIdentifiersMap.containsKey(boxIdentifier)) {
                boxIdentifiersMap.put(boxIdentifier, new ArrayList<MonitoringStatistics>());
            }
            boxIdentifiersMap.get(boxIdentifier).add(monitoringStatistics);
        }
        return map;
    }
}
