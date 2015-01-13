package com.griddynamics.jagger.engine.e1.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Loop scheduler that can execute one task with given period.
 * If you add new task - previous will be canceled.
 * You can change period of process at runtime.
 */
public class PeriodSingleTaskScheduler {

    private ScheduledExecutorService loopExecutor;
    private ExecutorService taskExecutor;

    /**
     * Last started schedule process, saved to be able to stop it */
    private ScheduledFuture lastStartedLoopProcess = null;
    private static final int DEFAULT_CORE_POOL_SIZE = 2;

    /**
     * Last started configuration */
    private volatile Configuration currentConfiguration;

    private Lock lock = new ReentrantLock();


    Logger log = LoggerFactory.getLogger(PeriodSingleTaskScheduler.class);


    public PeriodSingleTaskScheduler() {
        this(DEFAULT_CORE_POOL_SIZE);
    }

    public PeriodSingleTaskScheduler(int corePoolSize) {
        loopExecutor = Executors.newScheduledThreadPool(DEFAULT_CORE_POOL_SIZE);
        taskExecutor = Executors.newFixedThreadPool(5);
    }


    /**
     * Schedule command at fixed rate.
     * Cancel previously started process.
     *
     * @param command command to be executed every period
     * @param initialDelay initialDelay before executing command
     * @param period period
     * @param unit time unit for period, initialDelay
     */
    public void scheduleAtFixedRate(final Runnable command,
                                    long initialDelay,
                                    long period,
                                    TimeUnit unit) {

        lock.lock();
        try {
            Configuration newConfiguration = new Configuration(command, initialDelay, period, unit);

            if (!newConfiguration.equals(currentConfiguration)) {

                log.debug("Schedule new task {}", newConfiguration);
                currentConfiguration = newConfiguration;
                // cancel task if running
                if (lastStartedLoopProcess != null) {
                    // stop previous loop process, but do not interrupt
                    lastStartedLoopProcess.cancel(true);
                }

                lastStartedLoopProcess = loopExecutor.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        taskExecutor.submit(command);
                    }
                }, initialDelay, period, unit);
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * Change initialDelay, period at runtime
     *
     * @param delay new initialDelay
     * @param period new period
     * @param unit time unit for period, initialDelay
     */
    public void changePeriod(long delay, long period, TimeUnit unit) {
        lock.lock();
        try {
            if (currentConfiguration == null) {
                throw new NullPointerException("Has no previously started process");
            }

            scheduleAtFixedRate(currentConfiguration.getCommand(), delay, period, unit);
        } finally {
            lock.unlock();
        }
    }


    /**
     * Shutdown scheduler
     */
    public void shutdown() {
        loopExecutor.shutdownNow();
        taskExecutor.shutdownNow();
    }


    /**
     * Disable Task
     */
    public void clear() {
        if (lastStartedLoopProcess != null) {
            lastStartedLoopProcess.cancel(true);
        }
        currentConfiguration = null;
    }


    /**
     * Current configuration
     * @return current configuration, null if no task has been scheduled
     */
    public Configuration getCurrentConfiguration() {
        return currentConfiguration;
    }


    /**
     * Represents current configuration of loop process
     */
    public class Configuration {

        private final Runnable command;
        private final long initialDelay;
        private final long period;
        private final TimeUnit unit;

        public Configuration(Runnable command, long delay, long period, TimeUnit unit) {


            this.command = command;
            this.initialDelay = delay;
            this.period = period;
            this.unit = unit;
        }

        public Runnable getCommand() {
            return command;
        }

        public long getInitialDelay() {
            return initialDelay;
        }

        public long getPeriod() {
            return period;
        }

        public TimeUnit getUnit() {
            return unit;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Configuration that = (Configuration) o;

            if (initialDelay != that.initialDelay) return false;
            if (period != that.period) return false;
            if (command != null ? !command.equals(that.command) : that.command != null) return false;
            if (unit != that.unit) return false;

            return true;
        }
    }
}
