package com.griddynamics.jagger.engine.e1.collector.testsuite;

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.util.GeneralNodeInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 12/12/13
 * Time: 12:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestSuiteInfo {
    private String sessionId;
    private Map<NodeId,GeneralNodeInfo> generalNodeInfo = new HashMap<NodeId,GeneralNodeInfo>();

    private long duration;

    public TestSuiteInfo(String sessionId, Map<NodeId,GeneralNodeInfo> generalNodeInfo){
        this.sessionId = sessionId;
        this.generalNodeInfo = generalNodeInfo;
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

    /** Returns information about nodes where jagger kernels and agents are running
     * @author Gribov Kirill
     * @n
     * */
    public Map<NodeId, GeneralNodeInfo> getGeneralNodeInfo() { return generalNodeInfo; }

    public void setGeneralNodeInfo(Map<NodeId, GeneralNodeInfo> generalNodeInfo) { this.generalNodeInfo = generalNodeInfo; }
}
