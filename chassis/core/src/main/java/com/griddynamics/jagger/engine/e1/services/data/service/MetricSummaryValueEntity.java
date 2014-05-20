package com.griddynamics.jagger.engine.e1.services.data.service;

//??? docu

import com.griddynamics.jagger.util.Decision;

/** Class is a model of single point in metric detailed results (values vs time)
 *
 * @details
 * MetricPlotPointEntity is used to get test results from database with use of @ref DataService
 *
 * @author
 * Gribov Kirill
 */
public class MetricSummaryValueEntity {
    private Double value;
    //??? null => no decision
    private Decision decision = null;

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MetricSummaryValueEntity{" +
                "value=" + value +
                ", decision=" + decision +
                '}';
    }
}
