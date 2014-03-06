package com.griddynamics.jagger.webclient.server.rules;

public class Rule {

    protected String id;
    protected String displayName;
    protected String rule = null;

    public Rule() {}
    public Rule(String id, String displayName, String rule) {
        this.id = id;
        this.displayName = displayName;
        this.rule = rule;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRule() {
        return rule;
    }
}
