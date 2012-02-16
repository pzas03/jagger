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

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.facade.client.JaggerFacade;
import com.griddynamics.jagger.facade.client.JaggerFacadeService;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.layout.Layout;

import java.util.ArrayList;

/**
 * User: dkotlyarov
 */
public class LocationForm extends DynamicForm {
    private final LocationNode locationNode;
    private final ButtonItem startSessionButton = new ButtonItem();
    private final ButtonItem stopSessionButton = new ButtonItem();
    private final ButtonItem killSessionButton = new ButtonItem();
    private final HTMLPane logPane = new HTMLPane();
    private Timer timer = null;

    public LocationForm(final Layout layout, final LocationNode locationNode) {
        this.locationNode = locationNode;

        setWidth(250);

        final JaggerFacade facade = locationNode.getNavigationTree().getFacade();

        startSessionButton.setTitle("Start new session");
        startSessionButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                startSessionButton.disable();

                JaggerFacadeService.App.getInstance().startSession(facade.getUserId(),
                                                                   locationNode.getLocationDTO().getName(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                    }

                    @Override
                    public void onSuccess(Void result) {
                        locationNode.createForm();
                    }
                });
            }
        });

        stopSessionButton.setTitle("Stop session");
        stopSessionButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                stopSessionButton.disable();

                JaggerFacadeService.App.getInstance().stopSession(facade.getUserId(),
                                                                  locationNode.getLocationDTO().getName(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                    }

                    @Override
                    public void onSuccess(Void result) {
                        locationNode.createForm();
                    }
                });
            }
        });

        killSessionButton.setTitle("Kill session");
        killSessionButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                killSessionButton.disable();

                JaggerFacadeService.App.getInstance().killSession(facade.getUserId(),
                                                                  locationNode.getLocationDTO().getName(), new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                    }

                    @Override
                    public void onSuccess(Void result) {
                        locationNode.createForm();
                    }
                });
            }
        });

        JaggerFacadeService.App.getInstance().isSessionStarted(facade.getUserId(),
                                                               locationNode.getLocationDTO().getName(), new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Boolean result) {
                if (!result) {
                    setFields(startSessionButton);

                    layout.setMembers();
                    layout.addMember(LocationForm.this);
                    layout.redraw();
                } else {
                    JaggerFacadeService.App.getInstance().getFullSessionLog(facade.getUserId(),
                                                                            locationNode.getLocationDTO().getName(), new AsyncCallback<ArrayList<String>>() {
                        @Override
                        public void onFailure(Throwable caught) {
                        }

                        @Override
                        public void onSuccess(ArrayList<String> result) {
                            setFields(stopSessionButton, killSessionButton);

                            StringBuilder log = new StringBuilder(result.size() * 256);
                            for (String str : result) {
                                if (log.length() > 0) {
                                    log.append("<br>");
                                }
                                log.append(str);
                            }
                            logPane.setContents(log.toString());

                            layout.setMembers();
                            layout.addMember(LocationForm.this);
                            layout.addMember(logPane);
                            layout.redraw();

                            LocationForm.this.timer = new Timer() {
                                @Override
                                public void run() {
                                    JaggerFacadeService.App.getInstance().getSessionLog(facade.getUserId(),
                                                                                        locationNode.getLocationDTO().getName(), new AsyncCallback<ArrayList<String>>() {
                                        @Override
                                        public void onFailure(Throwable caught) {
                                        }

                                        @Override
                                        public void onSuccess(ArrayList<String> result) {
                                            StringBuilder log = new StringBuilder(logPane.getContents());
                                            for (String str : result) {
                                                if (log.length() > 0) {
                                                    log.append("<br>");
                                                }
                                                log.append(str);
                                            }
                                            logPane.setContents(log.toString());
                                        }
                                    });
                                }
                            };
                            LocationForm.this.timer.scheduleRepeating(1000);
                        }
                    });
                }
            }
        });
    }
}
