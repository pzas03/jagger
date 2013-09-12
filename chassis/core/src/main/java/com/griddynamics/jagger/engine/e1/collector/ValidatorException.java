package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.invoker.InvocationException;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 9/12/13
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValidatorException extends InvocationException {

    private Validator validator;
    private Object result;

    public ValidatorException(Validator validator, Object result) {
        this.validator = validator;
        this.result = result;
    }

    public Validator getValidator() {
        return validator;
    }

    public Object getResult() {
        return result;
    }
}
