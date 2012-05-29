package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class SessionDataDto implements Serializable {
    private String sessionId;
    private Date startDate;
    private Date endDate;
    private int activeKernelsCount;
    private int tasksExecuted;
    private int tasksFailed;

    public SessionDataDto() {
    }

    public SessionDataDto(String sessionId, Date startDate, Date endDate, int activeKernelsCount, int tasksExecuted, int tasksFailed) {
        this.sessionId = sessionId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activeKernelsCount = activeKernelsCount;
        this.tasksExecuted = tasksExecuted;
        this.tasksFailed = tasksFailed;
    }

    public String getName() {
        return "Session " + sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public int getActiveKernelsCount() {
        return activeKernelsCount;
    }

    public int getTasksExecuted() {
        return tasksExecuted;
    }

    public int getTasksFailed() {
        return tasksFailed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SessionDataDto)) return false;

        SessionDataDto that = (SessionDataDto) o;

        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SessionDataDto{" +
                "name='" + getName() + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", activeKernelsCount=" + activeKernelsCount +
                ", tasksExecuted=" + tasksExecuted +
                ", tasksFailed=" + tasksFailed +
                '}';
    }
}
