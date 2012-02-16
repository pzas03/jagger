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

package com.griddynamics.jagger.facade.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.SessionData;
import com.griddynamics.jagger.facade.Config;
import com.griddynamics.jagger.facade.Context;
import com.griddynamics.jagger.facade.Report;
import com.griddynamics.jagger.facade.User;
import com.griddynamics.jagger.facade.client.JaggerFacadeService;
import com.griddynamics.jagger.facade.client.navigation.LocationDTO;
import com.griddynamics.jagger.facade.client.navigation.ReportDTO;
import com.griddynamics.jagger.facade.client.navigation.ReportRequestDTO;
import com.griddynamics.jagger.facade.client.navigation.SessionDTO;
import net.sf.jasperreports.engine.JasperPrint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class JaggerFacadeServiceImpl extends RemoteServiceServlet implements JaggerFacadeService {
    private static final Logger log = LoggerFactory.getLogger(JaggerFacadeServiceImpl.class);

    public JaggerFacadeServiceImpl() {
    }

    @Override
    public String getUserId() {
        return getThreadLocalRequest().getSession().getId();
    }

    @Override
    public LocationDTO[] getLocations() {
        return Config.Location.toDTOs(User.getConfig().getLocations());
    }

    @Override
    public int getSessionCount(String userId, String locationName) {
        return User.getUser(userId).getContext(locationName).getSessionCount();
    }

    @Override
    public SessionDTO[] getSessions(String userId, String locationName) {
        List<SessionData> sessionDatas = User.getUser(userId).getContext(locationName).getSessions();
        SessionDTO[] sessionDTOs = new SessionDTO[sessionDatas.size()];
        int i = 0;
        for (SessionData sessionData : sessionDatas) {
            sessionDTOs[i++] = new SessionDTO(sessionData.getId(),
                                              sessionData.getSessionId(),
                                              sessionData.getStartTime().toString(),
                                              sessionData.getEndTime().toString(),
                                              sessionData.getTaskExecuted(),
                                              sessionData.getTaskFailed(),
                                              sessionData.getActiveKernels(),
                                              sessionData.getComment());
        }
        return sessionDTOs;
    }

    @Override
    public ReportDTO getReport(String userId, String locationName, String sessionId) {
        return User.getUser(userId).getContext(locationName).getReport(sessionId).toDTO();
    }

    @Override
    public String exportReport(String userId, String locationName, String sessionId) {
        User user = User.getUser(userId);
        Report report = user.getContext(locationName).getReport(sessionId);
        return Report.export(user, report.getPrint());
    }

    @Override
    public String exportReport(String userId, String locationName, ReportRequestDTO[] reportRequestDTOs) {
        User user = User.getUser(userId);
        Context context = user.getContext(locationName);
        JasperPrint print = null;
        for (ReportRequestDTO reportRequestDTO : reportRequestDTOs) {
            Report report = context.getReport(reportRequestDTO.getSessionId());
            if (print == null) {
                print = report.createPrint();
            }
            report.fillPrint(print, reportRequestDTO.getFramePaths());
        }
        return Report.export(user, print);
    }

    @Override
    public void startSession(String userId, String locationName) {
        User user = User.getUser(userId);
        Context context = user.getContext(locationName);
        context.startSession();
    }

    @Override
    public void stopSession(String userId, String locationName) {
        User user = User.getUser(userId);
        Context context = user.getContext(locationName);
        context.stopSession(false);
    }

    @Override
    public void killSession(String userId, String locationName) {
        User user = User.getUser(userId);
        Context context = user.getContext(locationName);
        context.stopSession(true);
    }

    @Override
    public boolean isSessionStarted(String userId, String locationName) {
        User user = User.getUser(userId);
        Context context = user.getContext(locationName);
        return context.isSessionStarted();
    }

    @Override
    public ArrayList<String> getFullSessionLog(String userId, String locationName) {
        User user = User.getUser(userId);
        Context context = user.getContext(locationName);
        return context.getFullSessionLog();
    }

    @Override
    public ArrayList<String> getSessionLog(String userId, String locationName) {
        User user = User.getUser(userId);
        Context context = user.getContext(locationName);
        return context.getSessionLog();
    }
}
