package com.griddynamics.jagger.engine.e1.scenario;

import com.google.common.collect.Maps;
import com.griddynamics.jagger.coordinator.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ExactInvocationsClock implements WorkloadClock {

    private static final Logger log = LoggerFactory.getLogger(ExactInvocationsClock.class);

    private int threadCount;

    private int samplesCount;

    public ExactInvocationsClock(int samplesCount, int threadCount) {
        this.samplesCount = samplesCount;
        this.threadCount = threadCount;
    }

    @Override
    public Map<NodeId, Integer> getPoolSizes(Set<NodeId> nodes) {
        int max = threadCount / nodes.size() + 1;
        Map<NodeId, Integer> result = Maps.newHashMap();
        for (NodeId node : nodes) {
            result.put(node, max);
        }
        return result;
    }

    @Override
    public void tick(WorkloadExecutionStatus status, WorkloadAdjuster adjuster) {
        log.debug("Going to perform tick with status {}", status);

        Set<NodeId> nodes = status.getNodes();
        int samplesLeft = samplesCount - status.getTotalSamples();
        if (samplesLeft <= 0) {
           // shutdown.set(true);
        } else {

        }
        LinkedHashMap<NodeId, WorkloadConfiguration> workloadConfigurations = new LinkedHashMap<NodeId, WorkloadConfiguration>(nodes.size());
        int maxSamples = samplesLeft/nodes.size();
        for (NodeId node : nodes) {
            WorkloadConfiguration workloadConfiguration = WorkloadConfiguration.with(status.getThreads(node) /*need active threads or something*/, status.getDelay(node), maxSamples);
            adjuster.adjustConfiguration(node, workloadConfiguration);
        }
    }

    @Override
    public int getTickInterval() {
        return 1000;
    }

    @Override
    public int getValue() {
        return samplesCount;
    }
}
