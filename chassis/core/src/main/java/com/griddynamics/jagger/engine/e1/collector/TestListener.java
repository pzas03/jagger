package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.engine.e1.collector.test.TestInfoStart;
import com.griddynamics.jagger.engine.e1.collector.test.TestInfoStatus;
import com.griddynamics.jagger.engine.e1.collector.test.TestInfoStop;

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
     * @param testInfo - describes start test properties*/
    void onStart(TestInfoStart testInfo);

    /** Executes after test stops
     * @author Gribov Kirill
     * @n
     *
     * @param testInfo - describes stop test information */
    void onStop(TestInfoStop testInfo);

    /** Executes during all test.
     * @author Gribov Kirill
     * @n
     * @par Details:
     * @details This method executes when Jagger calibrates current workload.
     * You can setup time between ticks with property 'workload.tickinterval.default'
     *
     * @param status - contains info about current number of threads, samples and etc.*/
    void onTick(TestInfoStatus status);

    public static class Composer implements TestListener {

        private Iterable<TestListener> listeners;

        public Composer(Iterable<TestListener> listeners) {
            this.listeners = listeners;
        }

        public static TestListener compose(Iterable<TestListener> collectors){
            return new Composer(collectors);
        }


        @Override
        public void onStart(TestInfoStart testInfo) {
            for (TestListener listener : listeners){
                listener.onStart(testInfo);
            }
        }

        @Override
        public void onStop(TestInfoStop testInfo) {
            for (TestListener listener : listeners){
                listener.onStop(testInfo);
            }
        }

        @Override
        public void onTick(TestInfoStatus testInfo) {
            for (TestListener listener : listeners){
                listener.onTick(testInfo);
            }
        }
    }
}