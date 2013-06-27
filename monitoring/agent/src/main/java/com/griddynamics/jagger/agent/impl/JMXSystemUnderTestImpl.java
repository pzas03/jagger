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

package com.griddynamics.jagger.agent.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.griddynamics.jagger.agent.model.DefaultMonitoringParameters;
import com.griddynamics.jagger.agent.model.SystemUnderTestInfo;
import com.griddynamics.jagger.agent.model.SystemUnderTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.griddynamics.jagger.util.Units.bytesToMiB;

/**
 * User: vshulga
 * Date: 7/5/11
 * Time: 4:45 PM
 * <p/>
 * JMX implementation of service for retrieving information about GC.
 * Could be used for multiple systems monitoring on the same host (portsForMonitoring).
 */
public class JMXSystemUnderTestImpl implements SystemUnderTestService {

    private final static Logger log = LoggerFactory.getLogger(JMXSystemUnderTestImpl.class);

    private static final String JMX_URL_TEMPLATE = "service:jmx:rmi:///jndi/rmi://%s/jmxrmi";
    private static final Collection<String> OLD_GEN_GC;
    private static final ObjectName GC_OBJECT_NAMES;

    private String jmxServices;
    private String name;
    private volatile Map<String, MBeanServerConnection> connections = Collections.emptyMap();

    static {
        OLD_GEN_GC = ImmutableSet.of("MarkSweepCompact", "PS MarkSweep", "ConcurrentMarkSweep", "G1 Old Generation");
        try {
            GC_OBJECT_NAMES = new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",*");
        } catch (MalformedObjectNameException e) {
            log.error("Error during JMX initializing", e);
            throw new RuntimeException(e);
        }
    }

    public void setJmxServices(String jmxServices) {
        this.jmxServices = jmxServices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Map<String, SystemUnderTestInfo> getInfo() {
        Set<String> identifiers = connections.keySet();
        Map<String, SystemUnderTestInfo> result = Maps.newHashMapWithExpectedSize(identifiers.size());

        for (String identifier : identifiers) {
            try {
                SystemUnderTestInfo info = analyzeJVM(identifier);
                result.put(identifier, info);
            } catch (Exception e) {
                log.error("Error in JMXSystemUnderTestImpl.analyzeJVM", e);
            }
        }
        return result;
    }

    public void init() {
        String[] jmxServicePorts = jmxServices.split(",");

        ImmutableMap.Builder<String, MBeanServerConnection> builder = ImmutableMap.builder();

        for (String service : jmxServicePorts) {
            try {
                JMXServiceURL serviceURL = new JMXServiceURL(String.format(JMX_URL_TEMPLATE, service));
                JMXConnector connector = JMXConnectorFactory.connect(serviceURL);
                MBeanServerConnection connection = connector.getMBeanServerConnection();

                builder.put(name + " collect from jmx port " + service, connection);
            } catch (IOException e) {
                log.error("Error during JMX initializing", e);
            }
        }

        connections = builder.build();

        if (connections.isEmpty()) {
            // TODO: replace it with specific exception for such situations when it is created.
            throw new RuntimeException("Error during JMX initialization. ZERO connections created for url "
                    + jmxServices + ".");
        }
    }

    private SystemUnderTestInfo analyzeJVM(String identifier) throws IOException {
        SystemUnderTestInfo result = new SystemUnderTestInfo(identifier);

        MBeanServerConnection connection = connections.get(identifier);

        MemoryMXBean memoryMXBean = ManagementFactory.newPlatformMXBeanProxy(connection,
                ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        result.putSysUTEntry(DefaultMonitoringParameters.HEAP_MEMORY_MAX, bytesToMiB(heapMemoryUsage.getMax()));
        result.putSysUTEntry(DefaultMonitoringParameters.HEAP_MEMORY_COMMITTED, bytesToMiB(heapMemoryUsage.getCommitted()));
        result.putSysUTEntry(DefaultMonitoringParameters.HEAP_MEMORY_USED, bytesToMiB(heapMemoryUsage.getUsed()));
        result.putSysUTEntry(DefaultMonitoringParameters.HEAP_MEMORY_INIT, bytesToMiB(heapMemoryUsage.getInit()));

        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        result.putSysUTEntry(DefaultMonitoringParameters.NON_HEAP_MEMORY_MAX, bytesToMiB(nonHeapMemoryUsage.getMax()));
        result.putSysUTEntry(DefaultMonitoringParameters.NON_HEAP_MEMORY_COMMITTED, bytesToMiB(nonHeapMemoryUsage.getCommitted()));
        result.putSysUTEntry(DefaultMonitoringParameters.NON_HEAP_MEMORY_USED, bytesToMiB(nonHeapMemoryUsage.getUsed()));
        result.putSysUTEntry(DefaultMonitoringParameters.NON_HEAP_MEMORY_INIT, bytesToMiB(nonHeapMemoryUsage.getInit()));

        long minorTime = 0;
        long majorTime = 0;
        long minorCount = 0;
        long majorCount = 0;
        Set<ObjectName> srvMemMgrNames = connection.queryNames(GC_OBJECT_NAMES, null);
        for (ObjectName gcMgr : srvMemMgrNames) {
            try {
                GarbageCollectorMXBean gcMgrBean =
                        ManagementFactory.newPlatformMXBeanProxy(connection, gcMgr.toString(),
                                GarbageCollectorMXBean.class);

                if (!gcMgrBean.isValid()) {
                    continue;
                }

                boolean majorCollector = OLD_GEN_GC.contains(gcMgrBean.getName());
                if (majorCollector) {
                    majorCount += gcMgrBean.getCollectionCount();
                    majorTime += gcMgrBean.getCollectionTime();
                } else {
                    minorCount += gcMgrBean.getCollectionCount();
                    minorTime += gcMgrBean.getCollectionTime();
                }
            } catch (IOException e) {
                log.error("Error in JMXSystemUnderTestImpl.analyzeJVM", e);
            }
        }
        result.putSysUTEntry(DefaultMonitoringParameters.JMX_GC_MAJOR_TIME, (double) majorTime);
        result.putSysUTEntry(DefaultMonitoringParameters.JMX_GC_MAJOR_UNIT, (double) majorCount);
        result.putSysUTEntry(DefaultMonitoringParameters.JMX_GC_MINOR_TIME, (double) minorTime);
        result.putSysUTEntry(DefaultMonitoringParameters.JMX_GC_MINOR_UNIT, (double) minorCount);

        return result;
    }
}
