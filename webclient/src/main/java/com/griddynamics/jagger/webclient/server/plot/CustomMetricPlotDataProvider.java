package com.griddynamics.jagger.webclient.server.plot;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.webclient.client.dto.*;
import com.griddynamics.jagger.webclient.server.ColorCodeGenerator;
import com.griddynamics.jagger.webclient.server.LegendProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 7/12/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomMetricPlotDataProvider implements PlotDataProvider{

    Logger log = LoggerFactory.getLogger(CustomMetricPlotDataProvider.class);

    private LegendProvider legendProvider;
    private EntityManager entityManager;

    public void setLegendProvider(LegendProvider legendProvider) {
        this.legendProvider = legendProvider;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public List<PlotNameDto> getPlotNames(TaskDataDto taskDataDto){
        List<String> plotNames = entityManager.createNativeQuery("select metricDetails.metric from MetricDetails metricDetails " +
                                                                 "where taskData_id in (:ids) " +
                                                                 "group by metricDetails.metric")
                                    .setParameter("ids", taskDataDto.getIds())
                                    .getResultList();
        if (plotNames.isEmpty())
            return Collections.emptyList();

        ArrayList<PlotNameDto> result = new ArrayList<PlotNameDto>(plotNames.size());

        for (String plotName : plotNames){
            if (plotName != null)
            result.add(new PlotNameDto(taskDataDto, plotName));
        }

        return result;
    }

    public Set<PlotNameDto> getPlotNames(List<TaskDataDto> taskDataDtos){

        Set<PlotNameDto> result = new HashSet<PlotNameDto>();

        Set<Long> testIds = new HashSet<Long>();
        for (TaskDataDto tdd : taskDataDtos) {
            testIds.addAll(tdd.getIds());
        }

        // check new model
        long temp = System.currentTimeMillis();
        List<Object[]> plotNamesNew = entityManager.createQuery(
                "select mpe.metricDescription.metricId, mpe.metricDescription.displayName, mpe.metricDescription.taskData.id " +
                "from MetricPointEntity as mpe where mpe.metricDescription.taskData.id in (:taskIds) group by mpe.metricDescription.id")
                .setParameter("taskIds", testIds)
                .getResultList();

        for (Object[] plotName : plotNamesNew){
            if (plotName != null) {
                for (TaskDataDto tdd : taskDataDtos) {
                    if (tdd.getIds().contains((Long)plotName[2])) {
                        result.add(new PlotNameDto(tdd, (String)plotName[0], (String)plotName[1]));
                    }
                }
            }
        }

        // check old model (before jagger 1.2.4)
        List<Object[]> plotNames = entityManager.createNativeQuery("select metricDetails.metric, metricDetails.taskData_id from MetricDetails metricDetails " +
                "where metricDetails.taskData_id in (:ids) " +
                "group by metricDetails.metric, metricDetails.taskData_id")
                .setParameter("ids", testIds)
                .getResultList();

        log.debug("{} ms spent to fetch custom metrics plots names", System.currentTimeMillis() - temp);

        for (Object[] plotName : plotNames){
            if (plotName != null) {
                for (TaskDataDto tdd : taskDataDtos) {
                    if (tdd.getIds().contains(((BigInteger)plotName[1]).longValue())) {
                        result.add(new PlotNameDto(tdd, (String)plotName[0]));
                    }
                }
            }
        }

        return result;
    }

    @Override
    public List<PlotSeriesDto> getPlotData(long taskId, PlotNameDto plotName) {
        return getPlotData(new HashSet<Long>(Arrays.asList(taskId)), plotName);
    }

    @Override
    public List<PlotSeriesDto> getPlotData(Set<Long> taskId, PlotNameDto plotNameDto) {

        String plotName = plotNameDto.getPlotName();
        String displayName = plotNameDto.getDisplay();

        long temp = System.currentTimeMillis();
        // check new way
        List<Object[]> metricValues = entityManager.createQuery(
                "select mpe.metricDescription.taskData.id, mpe.time, mpe.value, mpe.metricDescription.taskData.sessionId from MetricPointEntity as mpe " +
                "where mpe.metricDescription.taskData.id in (:taskIds) and mpe.metricDescription.metricId=:metricId")
                .setParameter("taskIds", taskId)
                .setParameter("metricId", plotName)
                .getResultList();

        log.debug("Fetch metric plot new in: {}", (System.currentTimeMillis() - temp));
        temp = System.currentTimeMillis();

        // check old way
        metricValues.addAll(
                entityManager.createQuery(
                        "select metrics.taskData.id, metrics.time, metrics.value, metrics.taskData.sessionId from MetricDetails metrics " +
                                "where metrics.metric=:plotName and metrics.taskData.id in (:taskIds)")
                        .setParameter("taskIds", taskId)
                        .setParameter("plotName", plotName)
                        .getResultList()
        );

        log.debug("Fetch metric plot old in: {}", (System.currentTimeMillis() - temp));

        if (metricValues.isEmpty())
            return Collections.emptyList();

        Multimap<Long, Object[]> metrics = ArrayListMultimap.create(taskId.size(), metricValues.size());
        List<PlotDatasetDto> plots = new ArrayList<PlotDatasetDto>();

        for (Object[] metricDetails : metricValues){
            metrics.put((Long)metricDetails[0], metricDetails);
        }

        for (Long id : metrics.keySet()){
            Collection<Object[]> taskMetrics = metrics.get(id);
            List<PointDto> points = new ArrayList<PointDto>(taskMetrics.size());
            String sessionId = null;
            //TaskData taskData = null;

            for (Object[] metricDetails : taskMetrics){
                //if (taskData == null) taskData = metricDetails.getTaskData();
                if (sessionId == null) sessionId = (String)metricDetails[3];
                points.add(new PointDto((Long)metricDetails[1] / 1000D, Double.parseDouble(metricDetails[2].toString())));
            }

            PlotDatasetDto plotDatasetDto = new PlotDatasetDto(points, legendProvider.generatePlotLegend(sessionId, displayName, true), ColorCodeGenerator.getHexColorCode());
            plots.add(plotDatasetDto);
        }

        PlotSeriesDto plotSeriesDto = new PlotSeriesDto(plots, "Time, sec", "", legendProvider.getPlotHeader(taskId, displayName));

        return Arrays.asList(plotSeriesDto);
    }
}
