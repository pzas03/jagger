package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class TaskDataDto implements Serializable {
    private long id;
    private String sessionId;
    private String taskName;
    private String status;

    public TaskDataDto() {
    }

    public TaskDataDto(long id, String sessionId, String taskName, String status) {
        this.id = id;
        this.sessionId = sessionId;
        this.taskName = taskName;
        this.status = status;
    }

    public long getId() {
        return id;
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
                ", sessionId='" + sessionId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
