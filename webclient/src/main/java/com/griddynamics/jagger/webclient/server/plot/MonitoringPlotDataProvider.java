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
public class MonitoringPlotDataProvider implements PlotDataProvider, SessionScopePlotDataProvider {
    private static final Logger log = LoggerFactory.getLogger(MonitoringPlotDataProvider.class);

    private Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups;
    private LegendProvider legendProvider;

    //==========Constructors

    public MonitoringPlotDataProvider(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups) {
        this.monitoringPlotGroups = monitoringPlotGroups;
    }

    //==========Getters & Setters

    public void setLegendProvider(LegendProvider legendProvider) {
        this.legendProvider = legendProvider;
    }

    //==========Contract Methods

    @Override
    public List<PlotSeriesDto> getPlotData(long taskId, String plotName) {
        EntityManager entityManager = EntityManagerProvider.getEntityManagerFactory().createEntityManager();

        List<PlotSeriesDto> plotSeriesDtoList;
        try {
            DefaultMonitoringParameters[] defaultMonitoringParametersGroup = findDefaultMonitoringParameters(monitoringPlotGroups, plotName);

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

            @SuppressWarnings("unchecked")
            List<MonitoringStatistics> monitoringStatisticsList = (List<MonitoringStatistics>) entityManager.createQuery(
                    "select ms from MonitoringStatistics as ms where ms.taskData = :monitoringTaskData " +
                            "and ms.parameterId.description in (:descrList)")
                    .setParameter("monitoringTaskData", monitoringTaskData)
                    .setParameter("descrList", monitoringParametersList)
                    .getResultList();

            plotSeriesDtoList = assemble(composeByBoxIdentifierAndDescription(monitoringStatisticsList), monitoringTaskData.getId(), plotName);
        } finally {
            entityManager.close();
        }

        return plotSeriesDtoList;
    }

    @Override
    public List<PlotSeriesDto> getPlotData(String sessionId, String plotName) {
        EntityManager entityManager = EntityManagerProvider.getEntityManagerFactory().createEntityManager();

        List<PlotSeriesDto> plotSeriesDtoList;
        try {
            DefaultMonitoringParameters[] defaultMonitoringParametersGroup = findDefaultMonitoringParameters(monitoringPlotGroups, plotName);

            log.debug("For plot {} there are exist {} monitoring parameters", plotName, defaultMonitoringParametersGroup);

            List<String> monitoringParametersList = new ArrayList<String>();
            for (DefaultMonitoringParameters defaultMonitoringParameter : defaultMonitoringParametersGroup) {
                monitoringParametersList.add(defaultMonitoringParameter.getDescription());
            }

            @SuppressWarnings("unchecked")
            List<MonitoringStatistics> monitoringStatisticsList = (List<MonitoringStatistics>) entityManager.createQuery(
                    "select ms from MonitoringStatistics as ms where sessionId=:sessionId and ms.parameterId.description in (:descrList) order by ms.taskData.number asc, ms.time asc")
                    .setParameter("sessionId", sessionId)
                    .setParameter("descrList", monitoringParametersList)
                    .getResultList();

            Map<String, Map<String, List<MonitoringStatistics>>> composedMap = composeByBoxIdentifierAndDescription(monitoringStatisticsList);
            plotSeriesDtoList = new ArrayList<PlotSeriesDto>();
            for (Map.Entry<String, Map<String, List<MonitoringStatistics>>> entry: composedMap.entrySet()) {
                List<PlotDatasetDto> plotDatasetDtoList = new ArrayList<PlotDatasetDto>();
                String boxIdentifier = entry.getKey();

                for (Map.Entry<String, List<MonitoringStatistics>> boxEntry : entry.getValue().entrySet()) {
                    String description = boxEntry.getKey();

                    int timeOffset = 0;
                    MonitoringStatistics previous = null;
                    List<PointDto> pointDtoList = new ArrayList<PointDto>();
                    for (MonitoringStatistics monitoringStatistics : boxEntry.getValue()) {
                        // If task was not first add time offset to time value
                        if (previous != null && previous.getTime() > monitoringStatistics.getTime()) {
                            timeOffset += previous.getTime();
                        }

                        double time = DataProcessingUtil.round((timeOffset+monitoringStatistics.getTime())/1000.0D);
                        double value = DataProcessingUtil.round(monitoringStatistics.getAverageValue());

                        pointDtoList.add(new PointDto(time, value));

                        previous = monitoringStatistics;
                    } //for

                    PlotDatasetDto plotDatasetDto = new PlotDatasetDto(pointDtoList, description, ColorCodeGenerator.getHexColorCode());
                    plotDatasetDtoList.add(plotDatasetDto);
                }
                plotSeriesDtoList.add(new PlotSeriesDto(plotDatasetDtoList, "Time, sec", "", "Session #" + sessionId + " " + plotName + " on " +boxIdentifier));
            }
        } finally {
            entityManager.close();
        }

        return plotSeriesDtoList;
    }

    //===========Auxiliary Methods

    protected Map<String, Map<String, List<MonitoringStatistics>>> composeByBoxIdentifierAndDescription(List<MonitoringStatistics> monitoringStatisticsList) {
        Map<String, Map<String, List<MonitoringStatistics>>> map = new HashMap<String, Map<String, List<MonitoringStatistics>>>();

        for (MonitoringStatistics monitoringStatistics : monitoringStatisticsList) {
            String boxIdentifier = monitoringStatistics.getBoxIdentifier() != null ? monitoringStatistics.getBoxIdentifier() : monitoringStatistics.getSystemUnderTestUrl();
            String description = monitoringStatistics.getParameterId().getDescription();

            if (!map.containsKey(boxIdentifier)) {
                map.put(boxIdentifier, new HashMap<String, List<MonitoringStatistics>>());
            }
            Map<String, List<MonitoringStatistics>> descriptionsMap = map.get(boxIdentifier);
            if (!descriptionsMap.containsKey(description)) {
                descriptionsMap.put(description, new ArrayList<MonitoringStatistics>());
            }
            descriptionsMap.get(description).add(monitoringStatistics);
        }
        return map;
    }

    protected List<PlotSeriesDto> assemble(Map<String, Map<String, List<MonitoringStatistics>>> composedMap, long monitoringTaskId, String plotName) {
        List<PlotSeriesDto> plotSeriesDtoList = new ArrayList<PlotSeriesDto>();
        for (Map.Entry<String, Map<String, List<MonitoringStatistics>>> entry: composedMap.entrySet()) {
            List<PlotDatasetDto> plotDatasetDtoList = new ArrayList<PlotDatasetDto>();
            String boxIdentifier = entry.getKey();

            for (Map.Entry<String, List<MonitoringStatistics>> boxEntry : entry.getValue().entrySet()) {
                String description = boxEntry.getKey();

                List<PointDto> pointDtoList = new ArrayList<PointDto>();
                for (MonitoringStatistics monitoringStatistics : boxEntry.getValue()) {
                    pointDtoList.add(new PointDto(DataProcessingUtil.round(monitoringStatistics.getTime()/1000.0D), DataProcessingUtil.round(monitoringStatistics.getAverageValue())));
                }

                if (pointDtoList.size() == 1) {
                    pointDtoList.add(new PointDto(0.0, 0.0));
                }

                PlotDatasetDto plotDatasetDto = new PlotDatasetDto(pointDtoList, description, ColorCodeGenerator.getHexColorCode());
                plotDatasetDtoList.add(plotDatasetDto);
            }
            plotSeriesDtoList.add(new PlotSeriesDto(plotDatasetDtoList, "Time, sec", "", legendProvider.getPlotHeader(monitoringTaskId, plotName + " on " +boxIdentifier)));
        }

        return plotSeriesDtoList;
    }

    private DefaultMonitoringParameters[] findDefaultMonitoringParameters(Map<GroupKey, DefaultMonitoringParameters[]> monitoringPlotGroups, String plotName) {
        for (Map.Entry<GroupKey, DefaultMonitoringParameters[]> entry: monitoringPlotGroups.entrySet()) {
            if (entry.getKey().getUpperName().equalsIgnoreCase(plotName)) {
                return entry.getValue();
            }
        }

        throw new IllegalStateException("Appropriate defaultMonitoringParameters array is not found in monitoringPlotGroups for plot name: "+plotName);
    }
}
