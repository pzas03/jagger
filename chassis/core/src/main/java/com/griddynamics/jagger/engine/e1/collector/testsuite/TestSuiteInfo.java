package com.griddynamics.jagger.engine.e1.collector.testsuite;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 12/12/13
 * Time: 12:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestSuiteInfo {
    private String sessionId;

    private long duration;

    public TestSuiteInfo(String sessionId){
        this.sessionId = sessionId;
    }

    /** Returns test-suite duration
     * @author Gribov Kirill
     * @n
     * */
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    /** Returns session id
     * @author Gribov Kirill
     * @n
     * */
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
