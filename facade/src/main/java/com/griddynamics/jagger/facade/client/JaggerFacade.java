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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.facade.client.navigation.NavigationTree;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import java.util.Date;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class JaggerFacade implements EntryPoint {
    private String userId = null;
    private final HLayout mainLayout = new HLayout();
    private final VLayout viewLayout = new VLayout();
    private long userAction = 0;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        userId = Cookies.getCookie("jagger_facade_user_id");
        if (userId == null) {
            JaggerFacadeService.App.getInstance().getUserId(new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                }

                @Override
                public void onSuccess(String result) {
                    userId = result;
                    Cookies.setCookie("jagger_facade_user_id", result, new Date(System.currentTimeMillis() + 157680000000L));
                    init();
                }
            });
        } else {
            init();
        }
    }

    private void init() {
        mainLayout.setWidth100();
        mainLayout.setHeight100();
        mainLayout.addMember(new NavigationTree(this));

        viewLayout.setWidth("70%");
        mainLayout.addMember(viewLayout);

        mainLayout.draw();
    }

    public String getUserId() {
        return userId;
    }

    public HLayout getMainLayout() {
        return mainLayout;
    }

    public VLayout getViewLayout() {
        return viewLayout;
    }

    public long getUserAction() {
        return userAction;
    }

    public long nextUserAction() {
        return ++userAction;
    }

    public boolean isLastUserAction(long userAction) {
        return userAction == getUserAction();
    }

    public void showStatus(Status status, String contents) {
        for (Canvas canvas : viewLayout.getMembers()) {
            if (canvas instanceof StatusControl) {
                StatusControl statusControl = (StatusControl) canvas;
                statusControl.setStatus(status, contents);
                return;
            }
        }

        StatusControl statusControl = new StatusControl(status, contents);
        viewLayout.setMembers();
        viewLayout.addMember(statusControl);
        viewLayout.redraw();
    }
}
