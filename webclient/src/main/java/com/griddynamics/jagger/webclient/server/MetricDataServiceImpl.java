package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.engine.e1.aggregator.workload.model.DiagnosticResultEntity;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.ValidationResultEntity;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessLatencyPercentile;
import com.griddynamics.jagger.webclient.client.MetricDataService;
import com.griddynamics.jagger.webclient.client.dto.MetricDto;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.MetricValueDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 08.04.13
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */
public class MetricDataServiceImpl implements MetricDataService {

    private static final Logger log = LoggerFactory.getLogger(MetricDataServiceImpl.class);
    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private HashMap<String, String> standardMetrics = new HashMap<String, String>();

    public MetricDataServiceImpl(){
        standardMetrics.put("Throughput", "throughput");
        standardMetrics.put("Latency", "avgLatency");
        standardMetrics.put("Duration", "totalDuration");
        standardMetrics.put("Success rate", "successRate");
        standardMetrics.put("Iterations", "samples");
    }

    @Override
    public Set<MetricNameDto> getMetricsNames(Set<TaskDataDto> tests) {
        HashSet<MetricNameDto> set = new HashSet<MetricNameDto>();
        for (TaskDataDto taskDataDto : tests){
            for (String standardMetricName : standardMetrics.keySet()){
                MetricNameDto metric = new MetricNameDto();
                metric.setName(standardMetricName);
                metric.setTests(taskDataDto);
                set.add(metric);
            }
            set.addAll(getLatencyMetricsNames(taskDataDto));
            set.addAll(getCustomMetricsNames(taskDataDto));
        }
        return set;
    }


    @Override
    public List<MetricDto> getMetrics(List<MetricNameDto> metricNames) {
        List<MetricDto> result = new ArrayList<MetricDto>(metricNames.size());
        for (MetricNameDto metricName : metricNames){
            result.add(getMetric(metricName));
        }
        return result;
    }

    @Override
    public MetricDto getMetric(MetricNameDto metricName) {

        MetricDto dto = new MetricDto();
        dto.setValues(new HashSet<MetricValueDto>());
        dto.setMetricName(metricName);

        if (standardMetrics.containsKey(metricName.getName())){
            //it is a standard metric
            List<Object[]> result = entityManager.createQuery("select workload."+ standardMetrics.get(metricName.getName())+", workload.sessionId "+
                                                                    "from WorkloadTaskData as workload " +
                                                              "where (workload.taskId, workload.sessionId) " +
                                                                    "in (select taskData.taskId, taskData.sessionId from TaskData as taskData where taskData.id in (:ids))").setParameter("ids", metricName.getTests().getIds()).getResultList();

            for (Object[] temp : result){
                String metricValue = temp[0].toString();
                long sessionId = Long.parseLong(temp[1].toString());

                MetricValueDto value = new MetricValueDto();
                value.setValue(metricValue);
                value.setSessionId(sessionId);

                dto.getValues().add(value);
            }
        }else{
            if (metricName.getName().matches("Latency .+ %")){
                //it is a latency metric
                Double latencyKey = Double.parseDouble(metricName.getName().split(" ")[1]);
                List<Object[]> latency = entityManager.createQuery("select s.percentileValue, s.workloadProcessDescriptiveStatistics.taskData.id, s.workloadProcessDescriptiveStatistics.taskData.sessionId " +
                                                                        "from  WorkloadProcessLatencyPercentile as s " +
                                                                   "where s.workloadProcessDescriptiveStatistics.taskData.id in (:taskIds) " +
                                                                          "and s.percentileKey=:latencyKey ")
                        .setParameter("taskIds", metricName.getTests().getIds())
                        .setParameter("latencyKey", latencyKey)
                        .getResultList();
                for (Object[] temp : latency){
                    MetricValueDto value = new MetricValueDto();
                    value.setValue(temp[0].toString());
                    value.setTestId(Long.parseLong(temp[1].toString()));
                    value.setSessionId(Long.parseLong(temp[2].toString()));
                    dto.getValues().add(value);
                }
            }else{
                //custom metric
                List<Object[]> metrics = entityManager.createQuery("select metric, metric.workloadData.sessionId " +
                                                                        "from DiagnosticResultEntity as metric " +
                                                                   "where metric.name=:name " +
                                                                            "and (metric.workloadData.taskId, metric.workloadData.sessionId) " +
                                                                                "in (select taskData.taskId, taskData.sessionId from TaskData as taskData where taskData.id in (:ids))").setParameter("ids", metricName.getTests().getIds()).setParameter("name", metricName.getName()).getResultList();

                if (!metrics.isEmpty()){
                    for (Object[] mas : metrics){
                        MetricValueDto value = new MetricValueDto();

                        DiagnosticResultEntity metric = (DiagnosticResultEntity)mas[0];
                        value.setValue(metric.getTotal().toString());

                        value.setSessionId(Long.parseLong(mas[1].toString()));
                        dto.getValues().add(value);
                    }
                }else{
                    List<Object[]> validators = entityManager.createQuery("select metric, metric.workloadData.sessionId " +
                                                                                "from ValidationResultEntity as metric " +
                                                                          "where metric.validator=:name " +
                                                                                   "and (metric.workloadData.taskId, metric.workloadData.sessionId) " +
                                                                                        "in (select taskData.taskId, taskData.sessionId from TaskData as taskData where taskData.id in (:ids))").setParameter("ids", metricName.getTests().getIds()).setParameter("name", metricName.getName()).getResultList();
                    for (Object[] mas : validators){
                        MetricValueDto value = new MetricValueDto();

                        ValidationResultEntity validator = (ValidationResultEntity)mas[0];

                        Integer notFailed = validator.getTotal()-validator.getFailed();
                        Double result = (double)notFailed/validator.getTotal();

                        String resultString = result.toString();
                        if (resultString.length() > 4){
                            resultString = resultString.substring(0, 4);
                        }
                        value.setValue(resultString);

                        value.setSessionId(Long.parseLong(mas[1].toString()));
                        dto.getValues().add(value);
                    }
                }
            }
        }

        return dto;
    }

    public Set<MetricNameDto> getCustomMetricsNames(TaskDataDto tests){
        Set<MetricNameDto> metrics;

        List<String> metricNames = entityManager.createQuery("select metric.name from DiagnosticResultEntity as metric where " +
                                                                "(metric.workloadData.taskId, metric.workloadData.sessionId) in " +
                                                                    "(select taskData.taskId, taskData.sessionId from TaskData as taskData where taskData.id in (:ids))").setParameter("ids", tests.getIds()).getResultList();

        List<String> validatorNames = entityManager.createQuery("select metric.validator from ValidationResultEntity as metric where " +
                                                                    "(metric.workloadData.taskId, metric.workloadData.sessionId) in " +
                                                                        "(select taskData.taskId, taskData.sessionId from TaskData as taskData where taskData.id in (:ids))").setParameter("ids", tests.getIds()).getResultList();

        metrics = new HashSet<MetricNameDto>(metricNames.size()+validatorNames.size());

        for (String name : metricNames){
            MetricNameDto metric = new MetricNameDto();
            metric.setTests(tests);
            metric.setName(name);

            metrics.add(metric);
        }

        for (String name : validatorNames){
            MetricNameDto validator = new MetricNameDto();
            validator.setTests(tests);
            validator.setName(name);

            metrics.add(validator);
        }

        return metrics;
    }

    public Set<MetricNameDto> getLatencyMetricsNames(TaskDataDto tests){
        Set<MetricNameDto> latencyNames;

        List<WorkloadProcessLatencyPercentile> latency = entityManager.createQuery(
                "select s from  WorkloadProcessLatencyPercentile as s where s.workloadProcessDescriptiveStatistics.taskData.id in (:taskIds) " +
                        "group by s.percentileKey " +
                        "having count(s.id)=:size")
                .setParameter("taskIds", tests.getIds())
                .setParameter("size", (long) tests.getIds().size())
                .getResultList();

        latencyNames = new HashSet<MetricNameDto>(latency.size());
        if (!latency.isEmpty()){
            for(WorkloadProcessLatencyPercentile percentile : latency) {
                MetricNameDto dto = new MetricNameDto();
                dto.setName("Latency "+Double.toString(percentile.getPercentileKey())+" %");
                dto.setTests(tests);
                latencyNames.add(dto);
            }
        }
        return latencyNames;
    }
}