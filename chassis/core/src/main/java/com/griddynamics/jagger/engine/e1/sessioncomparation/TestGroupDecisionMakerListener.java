package com.griddynamics.jagger.engine.e1.sessioncomparation;

import com.griddynamics.jagger.engine.e1.collector.test.TestInfo;
import com.griddynamics.jagger.engine.e1.collector.testgroup.TestGroupInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 2/6/14
 * Time: 11:08 AM
 * To change this template use File | Settings | File Templates.
 */
public interface TestGroupDecisionMakerListener {

    void onDecisionMaking(TestGroupInfo testInfo);

    public static class Composer implements TestGroupDecisionMakerListener{
        private static Logger log = LoggerFactory.getLogger(Composer.class);

        private List<TestGroupDecisionMakerListener> listenerList;

        private Composer(List<TestGroupDecisionMakerListener> listenerList){
            this.listenerList = listenerList;
        }

        @Override
        public void onDecisionMaking(TestGroupInfo testGroupInfo) {
            for (TestGroupDecisionMakerListener listener : listenerList){
                try{
                    listener.onDecisionMaking(testGroupInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on start in {} test-group-listener", listener.toString(), ex);
                }
            }
        }
        public static TestGroupDecisionMakerListener compose(List<TestGroupDecisionMakerListener> listeners){
            return new Composer(listeners);
        }
    }
}
