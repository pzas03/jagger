package com.griddynamics.jagger.engine.e1.collector.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Listener, that executes before test, after test and during all test
 * @author Gribov Kirill
 * @n
 * @par Details:
 * @details
 * @n
 * */
public interface TestListener{

    /** Executes before test starts
     * @author Gribov Kirill
     * @n
     *
     * @param testInfo - describes start test information*/
    void onStart(TestInfo testInfo);

    /** Executes after test stops
     * @author Gribov Kirill
     * @n
     *
     * @param testInfo - describes stop test information */
    void onStop(TestInfo testInfo);

    /** This method is periodically called while test is running. It shows current Jagger execution status(number of Jagger threads, etc)
     * @author Gribov Kirill
     * @n
     * @param status - contains info about current number of threads, samples and etc.*/
    void onRun(TestInfo status);

    public static class Composer implements TestListener {
        private static Logger log = LoggerFactory.getLogger(Composer.class);

        private Iterable<TestListener> listeners;

        public Composer(Iterable<TestListener> listeners) {
            this.listeners = listeners;
        }

        public static TestListener compose(Iterable<TestListener> collectors){
            return new Composer(collectors);
        }

        @Override
        public void onStart(TestInfo testInfo) {
            for (TestListener listener : listeners){
                try{
                    listener.onStart(testInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on start in {} test-listener", listener.toString(), ex);
                }
            }
        }

        @Override
        public void onStop(TestInfo testInfo) {
            for (TestListener listener : listeners){
                try{
                    listener.onStop(testInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on stop in {} test-listener", listener.toString(), ex);
                }
            }
        }

        @Override
        public void onRun(TestInfo testInfo) {
            for (TestListener listener : listeners){
                try{
                    listener.onRun(testInfo);
                }catch (RuntimeException ex){
                    log.error("Failed to call on run in {} test-listener", listener.toString(), ex);
                }
            }
        }
    }
}