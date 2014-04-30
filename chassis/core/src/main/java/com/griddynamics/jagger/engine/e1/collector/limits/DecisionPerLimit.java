package com.griddynamics.jagger.engine.e1.collector.limits;

import com.griddynamics.jagger.engine.e1.sessioncomparation.Decision;

import java.util.Set;

//??? docu
public class DecisionPerLimit {
    private Limit limit;
    private Set<DecisionPerMetric> decisionsPerMetric;
    private Decision decisionPerLimit;

    public DecisionPerLimit(Limit limit, Set<DecisionPerMetric> decisionsPerMetric, Decision decisionPerLimit) {
        this.limit = limit;
        this.decisionsPerMetric = decisionsPerMetric;
        this.decisionPerLimit = decisionPerLimit;
    }

    public Limit getLimit() {
        return limit;
    }

    public Set<DecisionPerMetric>  getDecisionsPerMetric() {
        return decisionsPerMetric;
    }

    public Decision getDecisionPerLimit() {
        return decisionPerLimit;
    }

    @Override
    public String toString() {
        return "DecisionPerLimit{" +
                "\n    limit=" + limit +
                "\n    decisionsPerMetric=" + decisionsPerMetric +
                "\n    decisionPerLimit=" + decisionPerLimit +
                '}';
    }

}
