package com.griddynamics.jagger.dbapi.dto;

import com.griddynamics.jagger.util.Decision;

import java.io.Serializable;

public class TaskDecisionDto implements Serializable {

    private Long taskId;
    private Decision decision;

    public TaskDecisionDto(Long taskId, Decision decision) {
        this.taskId = taskId;
        this.decision = decision;
    }

    public TaskDecisionDto() {}

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

}
