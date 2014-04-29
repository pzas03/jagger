package com.griddynamics.jagger.engine.e1.sessioncomparation;

import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;

import java.util.List;

/** Class, which contains information for decision making
 * @author Novozhilov Mark
 * @n
 * @par Details:
 * @details
 * @n
 * */

 public class DecisionMakerInfo {

    public List<WorkloadTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<WorkloadTask> tasks) {
        this.tasks = tasks;
    }

    //??? temp
    List<WorkloadTask> tasks;


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    //???
    String sessionId;


    //TODO add implementation

    //??? should contain
    // session Id
    // test names or ids
 }
