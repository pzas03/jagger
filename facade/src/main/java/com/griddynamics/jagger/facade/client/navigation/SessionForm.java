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

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.Layout;

/**
 * User: dkotlyarov
 */
public class SessionForm extends DynamicForm {
    private final SessionNode sessionNode;
    private final StaticTextItem sessionIdItem = new StaticTextItem();
    private final StaticTextItem startTimeItem = new StaticTextItem();
    private final StaticTextItem endTimeItem = new StaticTextItem();
    private final StaticTextItem taskExecutedItem = new StaticTextItem();
    private final StaticTextItem taskFailedItem = new StaticTextItem();
    private final StaticTextItem activeKernelsItem = new StaticTextItem();
    private final StaticTextItem commentItem = new StaticTextItem();

    public SessionForm(Layout layout, SessionNode sessionNode) {
        this.sessionNode = sessionNode;

        SessionDTO sessionDTO = sessionNode.getSessionDTO();

        setWidth(250);

        sessionIdItem.setTitle("Session");
        sessionIdItem.setValue(sessionDTO.getSessionId());

        startTimeItem.setTitle("Start time");
        startTimeItem.setValue(sessionDTO.getStartTime());

        endTimeItem.setTitle("End time");
        endTimeItem.setValue(sessionDTO.getEndTime());

        taskExecutedItem.setTitle("Task executed");
        taskExecutedItem.setValue(sessionDTO.getTaskExecuted());

        taskFailedItem.setTitle("Task failed");
        taskFailedItem.setValue(sessionDTO.getTaskFailed());

        activeKernelsItem.setTitle("Active kernels");
        activeKernelsItem.setValue(sessionDTO.getActiveKernels());

        commentItem.setTitle("Comment");
        commentItem.setValue(sessionDTO.getComment());

        setFields(sessionIdItem, startTimeItem, endTimeItem, taskExecutedItem, taskFailedItem, activeKernelsItem, commentItem);

        layout.setMembers();
        layout.addMember(this);
        layout.redraw();
    }

    public SessionNode getSessionNode() {
        return sessionNode;
    }

    public StaticTextItem getSessionIdItem() {
        return sessionIdItem;
    }

    public StaticTextItem getStartTimeItem() {
        return startTimeItem;
    }

    public StaticTextItem getEndTimeItem() {
        return endTimeItem;
    }

    public StaticTextItem getTaskExecutedItem() {
        return taskExecutedItem;
    }

    public StaticTextItem getTaskFailedItem() {
        return taskFailedItem;
    }

    public StaticTextItem getActiveKernelsItem() {
        return activeKernelsItem;
    }

    public StaticTextItem getCommentItem() {
        return commentItem;
    }
}
