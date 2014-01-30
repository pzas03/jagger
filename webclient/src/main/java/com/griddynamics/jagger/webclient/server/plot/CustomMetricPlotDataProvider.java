package com.griddynamics.jagger.webclient.server.plot;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.MetricDetails;
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


    public boolean isAvailable(String plotName) {
        BigInteger count = (BigInteger)entityManager.createNativeQuery("select count(id) FROM MetricDetails where metric=:plotName")
                            .setParameter("plotName", plotName)
                            .getSingleResult();

        if (count.intValue() > 0)
            return true;

        return false;
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
        temp = System.currentTimeMillis();
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
    public List<PlotSeriesDto> getPlotData(long taskId, String plotName) {
        return getPlotData(new HashSet<Long>(Arrays.asList(taskId)), plotName);
    }

    @Override
    public List<PlotSeriesDto> getPlotData(Set<Long> taskId, String plotName) {
        List<MetricDetails> metricValues = entityManager.createNativeQuery("select * from MetricDetails metrics " +
                                                                           "where metrics.metric=:plotName and metrics.taskData_id in (:taskIds)",
                                                                            MetricDetails.class)
                                            .setParameter("taskIds", taskId)
                                            .setParameter("plotName", plotName)
                                            .getResultList();

        if (metricValues.isEmpty())
            return Collections.emptyList();

        Multimap<Long, MetricDetails> metrics = ArrayListMultimap.create(taskId.size(), metricValues.size());
        List<PlotDatasetDto> plots = new ArrayList<PlotDatasetDto>();

        for (MetricDetails metricDetails : metricValues){
            metrics.put(metricDetails.getTaskData().getId(), metricDetails);
        }

        for (Long id : metrics.keySet()){
            Collection<MetricDetails> taskMetrics = metrics.get(id);
            List<PointDto> points = new ArrayList<PointDto>(taskMetrics.size());
            TaskData taskData = null;

            for (MetricDetails metricDetails : taskMetrics){
                if (taskData == null) taskData = metricDetails.getTaskData();
                points.add(new PointDto(metricDetails.getTime() / 1000D, metricDetails.getValue()));
            }

            PlotDatasetDto plotDatasetDto = new PlotDatasetDto(points, legendProvider.generatePlotLegend(taskData.getSessionId(), plotName, true), ColorCodeGenerator.getHexColorCode());
            plots.add(plotDatasetDto);
        }

        PlotSeriesDto plotSeriesDto = new PlotSeriesDto(plots, "Time, sec", "", legendProvider.getPlotHeader(taskId, plotName));

        return Arrays.asList(plotSeriesDto);
    }
}
