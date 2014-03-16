package com.griddynamics.jagger.webclient.server;

import java.util.List;
import java.util.Set;

/**
 * Created by kgribov on 3/7/14.
 */
public interface MetricDescriptionLoader {
    List<Object[]> loadTestsMetricDescriptions(Set<Long> ids);
}
