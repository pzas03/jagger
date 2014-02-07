package com.griddynamics.jagger.engine.e1.sessioncomparation;

import com.griddynamics.jagger.engine.e1.collector.test.TestInfo;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 2/6/14
 * Time: 11:08 AM
 * To change this template use File | Settings | File Templates.
 */
public interface TestGroupDecisionMakerListener {

    void onDecisionMaking(TestGroupInfo testInfo);

    public static class Composer implements TestGroupDecisionMakerListener {
        private static Logger log = LoggerFactory.getLogger(Composer.class);
        private TestGroupDecisionMakerListener listener;

        public Composer(TestGroupDecisionMakerListener listener) {
            this.listener = listener;
        }

        public static TestGroupDecisionMakerListener compose(TestGroupDecisionMakerListener collector){
            return new Composer(collector);
        }

        @Override
        public void onDecisionMaking(TestGroupInfo testInfo) {
                try{
                    listener.onDecisionMaking(testInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on run in {} test-group-decision-maker-listener", listener.toString(), ex);
                }
        }
    }

}
