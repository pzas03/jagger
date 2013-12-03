package com.griddynamics.jagger.engine.e1.collector.test;

import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/2/13
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class TestInfo {
    private WorkloadTask task;

    public WorkloadTask getTask() {
        return task;
    }

    public void setTask(WorkloadTask task) {
        this.task = task;
    }
}
