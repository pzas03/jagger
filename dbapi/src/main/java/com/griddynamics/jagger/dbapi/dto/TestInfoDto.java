package com.griddynamics.jagger.dbapi.dto;

import java.io.Serializable;

/**
 * User: amikryukov
 * Date: 2/14/14
 */
public class TestInfoDto implements Serializable {

    private String termination;
    private String clock;
    private String startTime;

    public String getTermination() {
        return termination;
    }

    public void setTermination(String termination) {
        this.termination = termination;
    }

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

}
