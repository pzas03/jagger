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

package com.griddynamics.jagger.master.configuration;

import com.google.common.collect.ImmutableList;
import com.griddynamics.jagger.engine.e1.scenario.UserClockConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.UserTerminateStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadTask;
import com.griddynamics.jagger.master.CompositableTask;
import com.griddynamics.jagger.master.CompositeTask;
import com.griddynamics.jagger.monitoring.InfiniteDuration;
import com.griddynamics.jagger.monitoring.MonitoringTask;
import com.griddynamics.jagger.user.ProcessingConfig;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: dkotlyarov
 */
public class UserTaskGenerator implements ApplicationContextAware {
    private ProcessingConfig config = getConfigFromCommandLine();
    private ApplicationContext applicationContext;

    public UserTaskGenerator() {
    }

    public ProcessingConfig getConfig() {
        return config;
    }

    public void setConfig(ProcessingConfig config) {
        this.config = config;
    }

    public List<Task> generate() {
        List<Task> result = new LinkedList<Task>();
        for (ProcessingConfig.Testing.Test testConfig : config.testing.tests) {
            WorkloadTask prototype = applicationContext.getBean(testConfig.main.task, WorkloadTask.class);
            WorkloadTask workloadTask = prototype.copy();
            workloadTask.setName(testConfig.name);

            AtomicBoolean shutdown = new AtomicBoolean(false);
            workloadTask.setTerminateStrategyConfiguration(new UserTerminateStrategyConfiguration(testConfig, shutdown));
            workloadTask.setClockConfiguration(new UserClockConfiguration(1000, testConfig, shutdown));

            Task task = workloadTask;
            if (config.monitoring.enabled) {
                CompositeTask composite = new CompositeTask();
                composite.setLeading(ImmutableList.<CompositableTask>of(workloadTask));

                MonitoringTask attendantMonitoring = new MonitoringTask(testConfig.name + " --- monitoring", composite.getTaskName(), new InfiniteDuration());
                composite.setAttendant(ImmutableList.<CompositableTask>of(attendantMonitoring));

                task = composite;
            }

            result.add(task);
        }
        return result;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static ProcessingConfig getConfigFromFile(String fileName) {
        try {
            return new Persister().read(ProcessingConfig.class, new File(fileName));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static ProcessingConfig getConfigFromCommandLine() {
        String processingConfigFileName = System.getProperty("jagger.user.processing.config");
        if (processingConfigFileName != null) {
            return getConfigFromFile(processingConfigFileName);
        } else {
            return null;
        }
    }
}
