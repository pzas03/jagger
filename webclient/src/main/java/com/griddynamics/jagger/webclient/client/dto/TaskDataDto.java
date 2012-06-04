package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class TaskDataDto implements Serializable {
    private long id;
    private String taskId;
    private String sessionId;
    private String taskName;
    private String status;

    public TaskDataDto() {
    }

    public TaskDataDto(long id, String taskId, String sessionId, String taskName, String status) {
        this.id = id;
        this.taskId = taskId;
        this.sessionId = sessionId;
        this.taskName = taskName;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getTaskId() {
        return taskId;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskDataDto)) return false;

        TaskDataDto that = (TaskDataDto) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "TaskDataDto{" +
                "id=" + id +
                ", taskId='" + taskId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
