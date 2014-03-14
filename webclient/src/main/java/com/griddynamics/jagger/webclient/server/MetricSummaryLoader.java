package com.griddynamics.jagger.webclient.server;

import java.util.List;
import java.util.Set;

/**
 * Created by kgribov on 3/14/14.
 */
public interface MetricSummaryLoader {
    List<Object[]> loadMetricSummary(Set<Long> taskIds, Set<String> metricId);
}
