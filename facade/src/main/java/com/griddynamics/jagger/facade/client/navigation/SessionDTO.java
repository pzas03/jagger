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

package com.griddynamics.jagger.facade.client.navigation;

import java.io.Serializable;

/**
 * User: dkotlyarov
 */
public class SessionDTO implements Serializable {
    private long id;
    private String sessionId;
    private String startTime;
    private String endTime;
    private int taskExecuted;
    private int taskFailed;
    private int activeKernels;
    private String comment;

    public SessionDTO() {
    }

    public SessionDTO(long id, String sessionId, String startTime, String endTime, int taskExecuted, int taskFailed, int activeKernels, String comment) {
        this.id = id;
        this.sessionId = sessionId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.taskExecuted = taskExecuted;
        this.taskFailed = taskFailed;
        this.activeKernels = activeKernels;
        this.comment = comment;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getTaskExecuted() {
        return taskExecuted;
    }

    public void setTaskExecuted(int taskExecuted) {
        this.taskExecuted = taskExecuted;
    }

    public int getTaskFailed() {
        return taskFailed;
    }

    public void setTaskFailed(int taskFailed) {
        this.taskFailed = taskFailed;
    }

    public int getActiveKernels() {
        return activeKernels;
    }

    public void setActiveKernels(int activeKernels) {
        this.activeKernels = activeKernels;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
