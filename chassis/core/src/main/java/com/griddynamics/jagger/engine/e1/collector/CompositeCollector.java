package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.ScenarioCollector;
import com.griddynamics.jagger.invoker.InvocationException;

/**
 * Created with IntelliJ IDEA.
 * User: nmusienko
 * Date: 22.03.13
 * Time: 11:58
 * To change this template use File | Settings | File Templates.
 */

public class CompositeCollector <Q, R, E> extends ScenarioCollector<Q, R, E> {

    private final ScenarioCollector<Q, R, E> simpleCollector;
    private final ScenarioCollector<Q, R, E> metricCollector;
    

    public CompositeCollector(String sessionId, String taskId, NodeContext kernelContext, ScenarioCollector<Q, R, E> simpleCollector, MetricCalculator metricCalculator, String name) {
        super(sessionId, taskId, kernelContext);
        this.simpleCollector = simpleCollector;
        this.metricCollector = new MetricCollector<Q, R, E>(sessionId, taskId, kernelContext, metricCalculator, name);
    }


    @Override
    public void flush() {
        simpleCollector.flush();
        metricCollector.flush();
    }

    @Override
    public void onStart(Q query, E endpoint) {
        simpleCollector.onStart(query, endpoint);
        metricCollector.onStart(query, endpoint);
    }

    @Override
    public void onSuccess(Q query, E endpoint, R result, long duration) {
        simpleCollector.onSuccess(query, endpoint, result, duration);
        metricCollector.onSuccess(query, endpoint, result, duration);
    }

    @Override
    public void onFail(Q query, E endpoint, InvocationException e) {
        simpleCollector.onFail(query, endpoint, e);
        metricCollector.onFail(query, endpoint, e);
    }

    @Override
    public void onError(Q query, E endpoint, Throwable error) {
        simpleCollector.onError(query, endpoint, error);
        metricCollector.onError(query, endpoint, error);
    }
}
