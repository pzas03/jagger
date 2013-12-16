package com.griddynamics.jagger.engine.e1.collector.testsuite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 12/12/13
 * Time: 12:10 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TestSuiteListener {
    void onStart(TestSuiteInfo testSuiteInfo);
    void onStop(TestSuiteInfo testSuiteInfo);

    public static class Composer implements TestSuiteListener {
        private static Logger log = LoggerFactory.getLogger(Composer.class);

        private Iterable<TestSuiteListener> listeners;

        public Composer(Iterable<TestSuiteListener> listeners) {
            this.listeners = listeners;
        }

        public static TestSuiteListener compose(Iterable<TestSuiteListener> collectors){
            return new Composer(collectors);
        }

        @Override
        public void onStart(TestSuiteInfo testSuiteInfo) {
            for (TestSuiteListener listener : listeners){
                try{
                    listener.onStart(testSuiteInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on start in {} test-suite-listener", listener.toString(), ex);
                }
            }
        }

        @Override
        public void onStop(TestSuiteInfo testSuiteInfo) {
            for (TestSuiteListener listener : listeners){
                try{
                    listener.onStop(testSuiteInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on stop in {} test-suite-listener", listener.toString(), ex);
                }
            }
        }
    }
}