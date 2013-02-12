package com.griddynamics.jagger.master.configuration;

/**
 * Created with IntelliJ IDEA.
 * User: nmusienko
 * Date: 12.02.13
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
public enum ConfigurationErrorStatus {

    EMPTY(null), TERMINATED("terminated"), WARNING("some tasks was failed");

    String message;

    private ConfigurationErrorStatus(String message){
        this.message=message;
    }

    public String getMessage() {
        return message;
    }
}
