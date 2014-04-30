package com.griddynamics.jagger.engine.e1.collector.limits;

import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.engine.e1.sessioncomparation.Decision;

import java.util.Set;

public class DecisionPerTest {
    private TestEntity testEntity;
    private Set<DecisionPerLimit> decisionsPerLimit;
    private Decision decisionPerTest;

    public DecisionPerTest(TestEntity testEntity, Set<DecisionPerLimit> decisionsPerLimit, Decision decisionPerTest) {
        this.testEntity = testEntity;
        this.decisionsPerLimit = decisionsPerLimit;
        this.decisionPerTest = decisionPerTest;
    }

    public TestEntity getTestEntity() {
        return testEntity;
    }

    public Set<DecisionPerLimit> getDecisionsPerLimit() {
        return decisionsPerLimit;
    }

    public Decision getDecisionPerTest() {
        return decisionPerTest;
    }

    @Override
    public String toString() {
        return "DecisionPerTest{" +
                "testName=" + testEntity.getName() +
                ", decisionPerTest=" + decisionPerTest +
                '}';
    }
}