package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class TaskDataDto implements Serializable {
    private long id;
    private int number;
    private String sessionId;
    private String taskName;
    private String status;

    public TaskDataDto() {
    }

    public TaskDataDto(long id, String sessionId, int number, String taskName, String status) {
        this.id = id;
        this.number = number;
        this.sessionId = sessionId;
        this.taskName = taskName;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "TaskDataDto{" +
                "id=" + id +
                ", number=" + number +
                ", sessionId='" + sessionId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
