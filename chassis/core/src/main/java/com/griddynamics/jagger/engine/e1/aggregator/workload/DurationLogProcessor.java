/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.engine.e1.aggregator.workload;

import com.caucho.hessian.io.Hessian2Input;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.TaskData;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.TimeInvocationStatistics;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadProcessDescriptiveStatistics;
import com.griddynamics.jagger.engine.e1.collector.DurationCollector;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.master.DistributionListener;
import com.griddynamics.jagger.master.Master;
import com.griddynamics.jagger.master.SessionIdProvider;
import com.griddynamics.jagger.master.configuration.Task;
import com.griddynamics.jagger.storage.FileStorage;
import com.griddynamics.jagger.storage.fs.logging.AggregationInfo;
import com.griddynamics.jagger.storage.fs.logging.DurationLogEntry;
import com.griddynamics.jagger.storage.fs.logging.LogAggregator;
import com.griddynamics.jagger.storage.fs.logging.LogProcessor;
import com.griddynamics.jagger.util.statistics.StatisticsCalculator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;

import java.io.EOFException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Alexey Kiselyov
 *         Date: 20.07.11
 */
public class DurationLogProcessor extends LogProcessor implements DistributionListener {

    private static final Logger log = LoggerFactory.getLogger(Master.class);

    private FileStorage fileStorage;
    private LogAggregator logAggregator;
    private SessionIdProvider sessionIdProvider;
    private int pointCount;

    @Required
    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    @Required
    public void setSessionIdProvider(SessionIdProvider sessionIdProvider) {
        this.sessionIdProvider = sessionIdProvider;
    }

    @Required
    public void setLogAggregator(LogAggregator logAggregator) {
        this.logAggregator = logAggregator;
    }

    @Required
    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public void onDistributionStarted(String sessionId, String taskId, Task task, Collection<NodeId> capableNodes) {
        // do nothing
    }

    @Override
    public void onTaskDistributionCompleted(String sessionId, String taskId, Task task) {
        if (task instanceof WorkloadTask) {
            processLog(sessionIdProvider.getSessionId(), taskId);
        }
    }

    private void processLog(String sessionId, String taskId) {
        Hessian2Input in = null;
        String file = null;
        try {
            String dir = sessionId + "/" + taskId + "/" + DurationCollector.DURATION_MARKER;
            file = dir + "/aggregated.dat";
            AggregationInfo aggregationInfo = logAggregator.chronology(dir, file);

            int intervalSize = (int) ((aggregationInfo.getMaxTime() - aggregationInfo.getMinTime()) / pointCount);
            if (intervalSize < 1) {
                intervalSize = 1;
            }

            in = new Hessian2Input(fileStorage.open(file));

            TaskData taskData = getTaskData(taskId, sessionId);
            if (taskData == null) {
                log.error("TaskData not found by taskId: {}", taskId);
                return;
            }

            StatisticsGenerator statisticsGenerator = new StatisticsGenerator(in, aggregationInfo, intervalSize, taskData).generate();
            final Collection<TimeInvocationStatistics> statistics = statisticsGenerator.getStatistics();
            final WorkloadProcessDescriptiveStatistics workloadProcessDescriptiveStatistics = statisticsGenerator.getWorkloadProcessDescriptiveStatistics();

            getHibernateTemplate().execute(new HibernateCallback<Void>() {
                @Override
                public Void doInHibernate(Session session) throws HibernateException, SQLException {
                    for (TimeInvocationStatistics stat : statistics) {
                        session.persist(stat);
                    }
                    session.persist(workloadProcessDescriptiveStatistics);
                    session.flush();
                    return null;
                }
            });

        } catch (Exception e) {
            log.error("Error during log processing", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
//                    fileStorage.delete(file, true);
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
    }

    private class StatisticsGenerator {
        private Hessian2Input in;
        private AggregationInfo aggregationInfo;
        private int intervalSize;
        private TaskData taskData;
        private Collection<TimeInvocationStatistics> statistics;
        private WorkloadProcessDescriptiveStatistics workloadProcessDescriptiveStatistics;

        public StatisticsGenerator(Hessian2Input in, AggregationInfo aggregationInfo, int intervalSize, TaskData taskData) {
            this.in = in;
            this.aggregationInfo = aggregationInfo;
            this.intervalSize = intervalSize;
            this.taskData = taskData;
        }

        public Collection<TimeInvocationStatistics> getStatistics() {
            return statistics;
        }

        public WorkloadProcessDescriptiveStatistics getWorkloadProcessDescriptiveStatistics() {
            return workloadProcessDescriptiveStatistics;
        }

        public StatisticsGenerator generate() throws IOException {
            statistics = new ArrayList<TimeInvocationStatistics>();

            long currentInterval = aggregationInfo.getMinTime() + intervalSize;
            long time = 0;
            int currentCount = 0;
            StatisticsCalculator windowStatisticsCalculator = new StatisticsCalculator();
            StatisticsCalculator globalStatisticsCalculator = new StatisticsCalculator();
            while (true) {
                try {
                    DurationLogEntry logEntry = (DurationLogEntry) in.readObject();

                    log.debug("Log entry {} time", logEntry.getTime());

                    while (logEntry.getTime() > currentInterval) {
                        log.debug("processing count {} interval {}", currentCount, intervalSize);

                        if (currentCount > 0) {
                            double throughput = (double) currentCount * 1000 / intervalSize;
                            statistics.add(assembleInvocationStatistics(time, windowStatisticsCalculator, throughput, taskData));
                            currentCount = 0;
                            windowStatisticsCalculator.reset();
                        } else {
                            statistics.add(new TimeInvocationStatistics(time, 0d, 0d, 0d, taskData));
                        }
                        time += intervalSize;
                        currentInterval += intervalSize;
                    }
                    currentCount++;
                    windowStatisticsCalculator.addValue(logEntry.getDuration());
                    globalStatisticsCalculator.addValue(logEntry.getDuration());
                } catch (EOFException e) {
                    break;
                }
            }

            if (currentCount > 0) {
                double throughput = (double) currentCount * 1000 / intervalSize;
                statistics.add(assembleInvocationStatistics(time, windowStatisticsCalculator, throughput, taskData));
            }

            workloadProcessDescriptiveStatistics = assembleDescriptiveScenarioStatistics(globalStatisticsCalculator, taskData);
            return this;
        }
    }
}
