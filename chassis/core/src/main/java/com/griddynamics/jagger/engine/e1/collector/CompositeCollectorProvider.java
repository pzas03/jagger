package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObjectProvider;
import com.griddynamics.jagger.engine.e1.scenario.ScenarioCollector;


/**
 * Created with IntelliJ IDEA.
 * User: nmusienko
 * Date: 22.03.13
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */
public class CompositeCollectorProvider<Q, R, E>  implements KernelSideObjectProvider<ScenarioCollector<Q, R, E>> {

    private KernelSideObjectProvider<ScenarioCollector<Q, R, E>> simpleCollectorProvider;
    private MetricCalculator<R> metricCalculator;
    private String name;

    public MetricCalculator<R> getMetricCalculator() {
        return metricCalculator;
    }

    public void setMetricCalculator(MetricCalculator<R> metricCalculator) {
        this.metricCalculator = metricCalculator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KernelSideObjectProvider<ScenarioCollector<Q, R, E>> getSimpleCollector() {
        return simpleCollectorProvider;
    }

    public void setSimpleCollector(KernelSideObjectProvider<ScenarioCollector<Q, R, E>> simpleCollector) {
        this.simpleCollectorProvider = simpleCollector;
    }

    @Override
    public ScenarioCollector<Q, R, E> provide(String sessionId, String taskId, NodeContext kernelContext) {
        return new CompositeCollector<Q, R, E>(
                sessionId,
                taskId,
                kernelContext,
                simpleCollectorProvider.provide(sessionId,taskId,kernelContext),
                metricCalculator,
                name);
    }
}
