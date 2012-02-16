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

package com.griddynamics.jagger.user;

import com.griddynamics.jagger.reporting.ReportingService;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: dkotlyarov
 */
@Root(name = "processing")
public class ProcessingConfig implements Serializable {
    @Element(name = "monitoring")
    public final Monitoring monitoring;

    @Element(name = "testing")
    public final Testing testing;

    @Element(name = "reporting")
    public final Reporting reporting;

    public ProcessingConfig(@Element(name = "monitoring") Monitoring monitoring,
                            @Element(name = "testing") Testing testing,
                            @Element(name = "reporting") Reporting reporting) {
        this.monitoring = monitoring;
        this.testing = testing;
        this.reporting = reporting;
    }

    public static class Monitoring implements Serializable {
        @Attribute(name = "enabled")
        public final boolean enabled;

        @Attribute(name = "diskFree")
        public final long diskFree;

        public Monitoring(@Attribute(name = "enabled") boolean enabled,
                          @Attribute(name = "diskFree") long diskFree) {
            this.enabled = enabled;
            this.diskFree = diskFree;
        }
    }

    public static class Testing implements Serializable {
        @Attribute(name = "enabled")
        public final boolean enabled;

        @ElementList(name = "tests", entry = "test", inline = true)
        public final List<Test> tests;

        public Testing(@Attribute(name = "enabled") boolean enabled,
                       @ElementList(name = "tests", entry = "test", inline = true) List<Test> tests) {
            this.enabled = enabled;
            this.tests = Collections.unmodifiableList(tests);
        }

        public static class Test implements Serializable {
            @Attribute(name = "name")
            public final String name;

            @Attribute(name = "duration", required = false)
            public final String duration;

            @Attribute(name = "seed", required = false)
            public final long seed;

            @Element(name = "main")
            public final Workload main;

            @Element(name = "additional")
            public final Workload additional;

            public Test(@Attribute(name = "name") String name,
                        @Attribute(name = "duration", required = false) String duration,
                        @Attribute(name = "seed", required = false) long seed,
                        @Element(name = "main") Workload main,
                        @Element(name = "additional") Workload additional) {
                this.name = name;
                this.duration = duration;
                this.seed = seed;
                this.main = main;
                this.additional = additional;
            }

            public static class Workload implements Serializable {
                @Attribute(name = "sample", required = false)
                public final String sample;

                @Attribute(name = "delay")
                public final String delay;

                @Attribute(name = "task")
                public final String task;

                @ElementList(name = "userGroups", entry = "userGroup", inline = true, required = false)
                public final List<UserGroup> userGroups;

                public Workload(@Attribute(name = "sample", required = false) String sample,
                                @Attribute(name = "delay") String delay,
                                @Attribute(name = "task") String task,
                                @ElementList(name = "userGroups", entry = "userGroup", inline = true, required = false) List<UserGroup> userGroups) {
                    this.sample = sample;
                    this.delay = delay;
                    this.task = task;
                    this.userGroups = Collections.unmodifiableList((userGroups != null) ? userGroups : new ArrayList<UserGroup>(0));
                }

                public static class UserGroup implements Serializable {
                    @Attribute(name = "count")
                    public final String count;

                    @Attribute(name = "startCount")
                    public final String startCount;

                    @Attribute(name = "startIn")
                    public final String startIn;

                    @Attribute(name = "startBy")
                    public final String startBy;

                    @Attribute(name = "life")
                    public final String life;

                    public UserGroup(@Attribute(name = "count") String count,
                                     @Attribute(name = "startCount") String startCount,
                                     @Attribute(name = "startIn") String startIn,
                                     @Attribute(name = "startBy") String startBy,
                                     @Attribute(name = "life") String life) {
                        this.count = count;
                        this.startCount = startCount;
                        this.startIn = startIn;
                        this.startBy = startBy;
                        this.life = life;
                    }
                }
            }
        }
    }

    public static class Reporting implements Serializable {
        @Attribute(name = "enabled")
        public final boolean enabled;

        @Attribute(name = "format")
        public final ReportingService.ReportType format;

        @Attribute(name = "file")
        public final String file;

        @Element(name = "task")
        public final Task task;

        @Element(name = "comparison")
        public final Comparison comparison;

        public Reporting(@Attribute(name = "enabled") boolean enabled,
                         @Attribute(name = "format") ReportingService.ReportType format,
                         @Attribute(name = "file") String file,
                         @Element(name = "task") Task task,
                         @Element(name = "comparison") Comparison comparison) {
            this.enabled = enabled;
            this.format = format;
            this.file = file;
            this.task = task;
            this.comparison = comparison;
        }

        public static class Task implements Serializable {
            @Attribute(name = "point")
            public final int point;

            @Attribute(name = "monitoringPoint")
            public final int monitoringPoint;

            public Task(@Attribute(name = "point") int point,
                        @Attribute(name = "monitoringPoint") int monitoringPoint) {
                this.point = point;
                this.monitoringPoint = monitoringPoint;
            }
        }

        public static class Comparison implements Serializable {
            @Attribute(name = "enabled")
            public final boolean enabled;

            @Attribute(name = "baseline")
            public final String baseline;

            @Attribute(name = "warningThreshold")
            public final double warningThreshold;

            @Attribute(name = "fatalThreshold")
            public final double fatalThreshold;

            public Comparison(@Attribute(name = "enabled") boolean enabled,
                              @Attribute(name = "baseline") String baseline,
                              @Attribute(name = "warningThreshold") double warningThreshold,
                              @Attribute(name = "fatalThreshold") double fatalThreshold) {
                this.enabled = enabled;
                this.baseline = baseline;
                this.warningThreshold = warningThreshold;
                this.fatalThreshold = fatalThreshold;
            }
        }
    }
}
