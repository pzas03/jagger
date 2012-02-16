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

package com.griddynamics.jagger.facade.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.griddynamics.jagger.facade.client.navigation.LocationDTO;
import com.griddynamics.jagger.facade.client.navigation.ReportDTO;
import com.griddynamics.jagger.facade.client.navigation.ReportRequestDTO;
import com.griddynamics.jagger.facade.client.navigation.SessionDTO;

import java.util.ArrayList;

@RemoteServiceRelativePath("JaggerFacadeService")
public interface JaggerFacadeService extends RemoteService {
    public String getUserId();
    public LocationDTO[] getLocations();
    public int getSessionCount(String userId, String locationName);
    public SessionDTO[] getSessions(String userId, String locationName);
    public ReportDTO getReport(String userId, String locationName, String sessionId);
    public String exportReport(String userId, String locationName, String sessionId);
    public String exportReport(String userId, String locationName, ReportRequestDTO[] reportRequestDTOs);
    public void startSession(String userId, String locationName);
    public void stopSession(String userId, String locationName);
    public void killSession(String userId, String locationName);
    public boolean isSessionStarted(String userId, String locationName);
    public ArrayList<String> getFullSessionLog(String userId, String locationName);
    public ArrayList<String> getSessionLog(String userId, String locationName);

    /**
     * Utility/Convenience class.
     * Use JaggerFacadeService.App.getInstance() to access static instance of JaggerFacadeServiceAsync
     */
    public static class App {
        private static JaggerFacadeServiceAsync ourInstance = GWT.create(JaggerFacadeService.class);

        public static synchronized JaggerFacadeServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
