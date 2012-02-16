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

package com.macys.platform;

import com.griddynamics.jagger.master.HibernateSessionIdProvider;
import com.macys.platform.services.management.dto.clusterinfo.ArtifactInfo;
import com.macys.platform.services.management.util.ClusterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;


public class SdpSessionComment implements InitializingBean, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(SdpSessionComment.class);

    private ApplicationContext applicationContext;

    private ClusterInfo clusterInfo;

    @Override
    public void afterPropertiesSet() throws Exception {

        HibernateSessionIdProvider sessionIdProvider = (HibernateSessionIdProvider) applicationContext.getBean("sequenceSessionIdProvider");

        sessionIdProvider.setSessionComment("war running: " + getWarInfo());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private String getWarInfo() {

        try {
            List<ArtifactInfo> warInfo = clusterInfo.getSdpArtifactsInfo();
            String result;

            for (ArtifactInfo artifactInfo : warInfo) {
                if (artifactInfo.getArtifactId().equals("sdp.war") || artifactInfo.getArtifactId().equals("sdp.web")) {
                    result = artifactInfo.getVersion() + "; " + artifactInfo.getReleaseDate();
                    return result;
                }
            }
        } catch (Exception e) {
            log.warn("Cant get cluster info {}", e);
        }

        return null;
    }

    public void setClusterInfo(ClusterInfo clusterInfo) {
        this.clusterInfo = clusterInfo;
    }
}
