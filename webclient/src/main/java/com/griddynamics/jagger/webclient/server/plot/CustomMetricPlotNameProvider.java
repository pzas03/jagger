package com.griddynamics.jagger.webclient.server.plot;

import com.griddynamics.jagger.webclient.client.dto.*;
import com.griddynamics.jagger.webclient.server.DataProcessingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 7/12/13
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomMetricPlotNameProvider {

    Logger log = LoggerFactory.getLogger(CustomMetricPlotNameProvider.class);

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Set<MetricNameDto> getPlotNames(List<TaskDataDto> taskDataDtos){

        long temp = System.currentTimeMillis();
        Set<MetricNameDto> result = new HashSet<MetricNameDto>();

        result.addAll(getPlotNamesNewModel(taskDataDtos));

        result.addAll(getPlotNamesOldModel(taskDataDtos));

        log.debug("{} ms spent to fetch custom metrics plots names in count of {}", System.currentTimeMillis() - temp, result.size());

        return result;
    }


    public Set<MetricNameDto> getPlotNamesOldModel(List<TaskDataDto> taskDataDtos){

        Set<Long> testIds = new HashSet<Long>();
        for (TaskDataDto tdd : taskDataDtos) {
            testIds.addAll(tdd.getIds());
        }

        // check old model (before jagger 1.2.4)
        List<Object[]> plotNames = entityManager.createNativeQuery("select metricDetails.metric, metricDetails.taskData_id from MetricDetails metricDetails " +
                "where metricDetails.taskData_id in (:ids) " +
                "group by metricDetails.metric, metricDetails.taskData_id")
                .setParameter("ids", testIds)
                .getResultList();


        if (plotNames.isEmpty()) {
            return Collections.emptySet();
        }

        Set<MetricNameDto> result = new HashSet<MetricNameDto>(plotNames.size());

        for (Object[] plotName : plotNames){
            if (plotName != null) {
                for (TaskDataDto tdd : taskDataDtos) {
                    if (tdd.getIds().contains(((BigInteger)plotName[1]).longValue())) {
                        MetricNameDto metricNameDto = new MetricNameDto(tdd, (String)plotName[0]);
                        metricNameDto.setOrigin(MetricNameDto.Origin.METRIC);
                        result.add(metricNameDto);
                    }
                }
            }
        }

        return result;
    }


    public Set<MetricNameDto> getPlotNamesNewModel(List<TaskDataDto> taskDataDtos){

        try {
            Set<Long> testIds = new HashSet<Long>();
            for (TaskDataDto tdd : taskDataDtos) {
                testIds.addAll(tdd.getIds());
            }

            // check new model
            List<Object[]> plotNamesNew = entityManager.createQuery(
                    "select mpe.metricDescription.metricId, mpe.metricDescription.displayName, mpe.metricDescription.taskData.id " +
                            "from MetricPointEntity as mpe where mpe.metricDescription.taskData.id in (:taskIds) group by mpe.metricDescription.id")
                    .setParameter("taskIds", testIds)
                    .getResultList();

            if (plotNamesNew.isEmpty()) {
                return Collections.emptySet();
            }

            Set<MetricNameDto> result = new HashSet<MetricNameDto>(plotNamesNew.size());

            for (Object[] plotName : plotNamesNew){
                if (plotName != null) {
                    for (TaskDataDto tdd : taskDataDtos) {
                        if (tdd.getIds().contains((Long)plotName[2])) {
                            result.add(new MetricNameDto(tdd, (String)plotName[0], (String)plotName[1], MetricNameDto.Origin.METRIC));
                        }
                    }
                }
            }

            return result;
        } catch (PersistenceException e) {
            log.debug("Could not fetch metric plot names from MetricPointEntity: {}", DataProcessingUtil.getMessageFromLastCause(e));
            return Collections.emptySet();
        }
    }
}
