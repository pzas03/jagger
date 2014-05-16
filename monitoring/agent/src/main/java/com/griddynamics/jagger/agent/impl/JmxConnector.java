package com.griddynamics.jagger.agent.impl;

import com.google.common.collect.Maps;
import com.griddynamics.jagger.util.AgentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.util.Map;

public class JmxConnector {
    private final static Logger log = LoggerFactory.getLogger(JMXSystemUnderTestImpl.class);

    //??? use timeout class
    private long connectionTimeout;
    private long connectionPeriod;
    private String jmxServices;
    private String urlFormat;


    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    @Required
    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public long getConnectionPeriod() {
        return connectionPeriod;
    }

    @Required
    public void setConnectionPeriod(long connectionPeriod) {
        this.connectionPeriod = connectionPeriod;
    }

    public String getJmxServices() {
        return jmxServices;
    }

    @Required
    public void setJmxServices(String jmxServices) {
        this.jmxServices = jmxServices;
    }

    public String getUrlFormat() {
        return urlFormat;
    }

    @Required
    public void setUrlFormat(String urlFormat) {
        this.urlFormat = urlFormat;
    }


    public Map<String, MBeanServerConnection> connect(String name) {
        Exception exception = null;
        Map<String, MBeanServerConnection> connections = Maps.newHashMap();
        long startTime = System.currentTimeMillis();
        long lifeTime = 0;

        while (lifeTime < connectionTimeout) {
            try {
                exception = null;

                log.info("Initializing JMX connection for {}. Url(s): {}",name,jmxServices);
                connections = AgentUtils.getMBeanConnections(
                        AgentUtils.getJMXConnectors(AgentUtils.splitServices(jmxServices), name + " collect from jmx port ", urlFormat)
                );

                if (connections.size() > 0) {
                    break;
                }
            } catch (IOException e) {
                exception = e;
            }

            try {
                log.info("Wait for next try to initialize JMX connection for {} ms", connectionPeriod);
                Thread.sleep(connectionPeriod);
            }
            catch (InterruptedException e) {
                exception = e;
                log.warn("JMX initialization interrupted");
            }

            lifeTime = System.currentTimeMillis() - startTime;
        }

        if (connections.size() == 0) {
            log.error("JMX connection was not established in {} ms",lifeTime);
            if (exception != null) {
                log.error("Error during JMX initializing",exception);
            }

            throw new RuntimeException("Error during JMX initialization. ZERO connections created for url "
                    + jmxServices + ".");
        }

        return connections;
    }
}
