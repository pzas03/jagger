package com.griddynamics.jagger.engine.e1.reporting;


//??? docu
public class SummaryDto {
    private String key;
    private String value;
    private String decision = "";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

}
