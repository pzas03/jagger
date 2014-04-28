package com.griddynamics.jagger.engine.e1;

import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.engine.e1.sessioncomparation.DecisionMakerInfo;
import com.griddynamics.jagger.engine.e1.sessioncomparation.TestGroupDecisionMakerListener;

public class BasicTGDecisionMakerListener extends ServicesAware implements Provider<TestGroupDecisionMakerListener> {
    //???private static final Logger log = LoggerFactory.getLogger(BasicTGDecisionMakerListener.class);

    @Override
    protected void init() {

    }

    @Override
    public TestGroupDecisionMakerListener provide() {
        return new TestGroupDecisionMakerListener() {
            @Override
            public void onDecisionMaking(DecisionMakerInfo decisionMakerInfo) {
                //???
                System.out.println(BasicTGDecisionMakerListener.class);

            }
        };
    }

}
