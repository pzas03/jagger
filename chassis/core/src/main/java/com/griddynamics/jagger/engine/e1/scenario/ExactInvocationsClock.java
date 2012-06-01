package com.griddynamics.jagger.engine.e1.scenario;

import com.google.common.collect.Maps;
import com.griddynamics.jagger.coordinator.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class ExactInvocationsClock implements WorkloadClock {

    private static final Logger log = LoggerFactory.getLogger(ExactInvocationsClock.class);

    private int threadCount;

    private int samplesCount;

    private int samplesSubmitted;

    private int totalSamples;

    private int delay;

    public ExactInvocationsClock(int samplesCount, int threadCount, int delay) {
        this.samplesCount = samplesCount;
        this.threadCount  = threadCount;
        this.delay        = delay;
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

        int samplesPerTick = status.getTotalSamples() - totalSamples;

        totalSamples = status.getTotalSamples();

        int samplesLeft = samplesCount - samplesSubmitted;
        if (samplesLeft <= 0) {
            return;
        }

        if (status.getTotalSamples() < samplesSubmitted / 2) {
            return;
        }

        Set<NodeId> nodes = status.getNodes();
        int threads =  threadCount / nodes.size();
        int samplesToAdd = (samplesLeft < samplesPerTick * 2) ? samplesLeft : samplesLeft / 2;
        int samplesPerNode  = (samplesSubmitted + samplesToAdd) / nodes.size();
        int restSamples = samplesToAdd % nodes.size();
        int s = 0;
        for (NodeId node : nodes) {
            int samples = samplesPerNode;
            if (restSamples > 0) {
                samples++;
                restSamples--;
            }
            WorkloadConfiguration workloadConfiguration = WorkloadConfiguration.with(threads, delay, samples);
            adjuster.adjustConfiguration(node, workloadConfiguration);
            s += samples;
        }
        samplesSubmitted = s;
    }

    @Override
    public int getTickInterval() {
        return 1000;
    }

    @Override
    public int getValue() {
        return samplesCount;
    }

    @Override
    public String toString() {
        return String.format("%d invocations; %d threads; %dms delay", samplesCount, threadCount, delay);
    }
}
