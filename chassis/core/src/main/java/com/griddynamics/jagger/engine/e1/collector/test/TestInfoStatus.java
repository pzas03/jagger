package com.griddynamics.jagger.engine.e1.collector.test;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/2/13
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestInfoStatus extends TestInfo{
    private int threads;
    private int samples;
    private int startedSamples;

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getSamples() {
        return samples;
    }

    public void setSamples(int samples) {
        this.samples = samples;
    }

    public int getStartedSamples() {
        return startedSamples;
    }

    public void setStartedSamples(int startedSamples) {
        this.startedSamples = startedSamples;
    }
}
