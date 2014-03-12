package com.griddynamics.jagger.webclient.server.fetch;

import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


public abstract class ConcurrentMetricDataFetcher<R> extends MetricDataFetcher<R> {

    protected int maxSizeOfBatch = 10000;

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    protected ExecutorService threadPool;

    public void setMaxSizeOfBatch(int maxSizeOfBatsh) {
        this.maxSizeOfBatch = maxSizeOfBatsh;
    }

    @Required
    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    public Set<R> getResult() {

        List<Future<Set<R>>> futureList = new ArrayList<Future<Set<R>>>();

        int fromIndex = 0;

        while (fromIndex < metricNames.size()) {
            int toIndex = fromIndex + maxSizeOfBatch;
            if (toIndex > metricNames.size()) {
                toIndex = metricNames.size();
            }

            futureList.add(threadPool.submit(new MetricDtoCallable(metricNames.subList(fromIndex, toIndex))));

            fromIndex += maxSizeOfBatch;
        }

        Set<R> result = new HashSet<R>();

        try {
            for (Future<Set<R>> future : futureList) {
                result.addAll(future.get());
            }
        } catch (Exception e) {
            log.error("Exception while fetching data", e);
            throw new RuntimeException("Exception while getting data from future" , e);
        }

        return result;
    }

    private class MetricDtoCallable implements Callable<Set<R>> {

        private List<MetricNameDto> metricNames;

        private MetricDtoCallable(List<MetricNameDto> metricNames) {
            this.metricNames = metricNames;
        }

        @Override
        public Set<R> call() throws Exception {
            return fetchData(metricNames);
        }
    }

    /**
     * method that executes with each callable
     * @param metricNames list of metricNames that concrete callable would process
     * @return List of results
     */
    protected abstract Set<R> fetchData(List<MetricNameDto> metricNames);
}
