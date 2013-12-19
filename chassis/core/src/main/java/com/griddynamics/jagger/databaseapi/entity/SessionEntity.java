package com.griddynamics.jagger.databaseapi.entity;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/5/13
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionEntity {
    private String id;
    private String comment;
    private Long startTime;
    private Long endTime;
    private Integer kernels;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getKernels() {
        return kernels;
    }

    public void setKernels(Integer kernels) {
        this.kernels = kernels;
    }
}
