package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.engine.e1.collector.test.TestInfoStart;
import com.griddynamics.jagger.engine.e1.collector.test.TestInfoStatus;
import com.griddynamics.jagger.engine.e1.collector.test.TestInfoStop;
import com.griddynamics.jagger.engine.e1.services.AbstractServicesAwareProvider;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/2/13
 * Time: 4:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class CollectThreadsTestListener extends AbstractServicesAwareProvider<TestListener>{

    private String metricId = "Jagger.Threads";

    @Override
    protected void init() {
        getMetricService().createMetric(new MetricDescription(metricId)
                                                .plotData(true)
                                                .showSummary(true)
                                                .addAggregator(new AvgMetricAggregatorProvider()));
    }

    @Override
    public TestListener provide() {
        return new TestListener() {
            @Override
            public void onStart(TestInfoStart testInfo) {}

            @Override
            public void onStop(TestInfoStop testInfo) {}

            @Override
            public void onTick(TestInfoStatus status) {
                getMetricService().saveValue(metricId, status.getThreads());
            }
        };
    }
}
