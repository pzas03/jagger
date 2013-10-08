package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.*;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;

import java.util.Arrays;
import java.util.List;

public class SuccessRateCollectorProvider<Q, R, E> extends MetricCollectorProvider<Q, R, E> {

    @Override
    public void init(String sessionId, String taskId, NodeContext kernelContext) {
        KeyValueStorage storage = kernelContext.getService(KeyValueStorage.class);
        storage.put(Namespace.of(
                sessionId, taskId, "metricAggregatorProviders"),
                "SR",
                aggregators
        );
    }

    @Override
    public SuccessRateCollector<Q, R, E> provide(String sessionId, String taskId, NodeContext kernelContext) {
        return new SuccessRateCollector(sessionId, taskId, kernelContext);
    }

    @Override
    public void setAggregators(List<MetricDescriptionEntry> aggregators) {
        this.aggregators = aggregators;
    }

    @Override
    public List<MetricDescriptionEntry> getAggregators() {
        return aggregators;
    }

    private List<MetricDescriptionEntry> aggregators;
//    private List<MetricDescriptionEntry> aggregators = Arrays.asList(
//            new MetricCollectorProvider.MetricDescriptionEntry(new SuccessRateAggregatorProvider(), true),
//            new MetricCollectorProvider.MetricDescriptionEntry(new SuccessRateFailsAggregatorProvider(), true));

}



