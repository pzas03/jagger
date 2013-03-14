package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 12.03.13
 * Time: 16:29
 * To change this template use File | Settings | File Templates.
 */
public class FalseValidator extends ResponseValidator {

    public FalseValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "false validator";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean validate(Object query, Object endpoint, Object result, long duration) {
        return false;
    }
}
