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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.facade.client.JaggerFacade;
import com.griddynamics.jagger.facade.client.JaggerFacadeService;
import com.griddynamics.jagger.facade.client.Status;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.NodeClickEvent;
import com.smartgwt.client.widgets.tree.events.NodeClickHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * User: dkotlyarov
 */
public class SessionNode extends TreeNode implements NodeClickHandler {
    private final LocationNode locationNode;
    private final SessionDTO sessionDTO;
    private boolean ready = false;

    public SessionNode(LocationNode locationNode, SessionDTO sessionDTO) {
        this.locationNode = locationNode;
        this.sessionDTO = sessionDTO;

        setAttribute("id", locationNode.getAttribute("id") + ", Session: " + sessionDTO.getSessionId());

        String comment = sessionDTO.getComment();
        if ((comment != null) && (!comment.isEmpty())) {
            setAttribute("name", "Session-" + sessionDTO.getSessionId() + " [" + sessionDTO.getStartTime() + "; " + comment + "]");
        } else {
            setAttribute("name", "Session-" + sessionDTO.getSessionId() + " [" + sessionDTO.getStartTime() + "]");
        }

        locationNode.getNavigationTree().getTree().add(this, locationNode);
    }

    public void delete() {
        locationNode.getNavigationTree().getTree().remove(this);
    }

    public boolean hasChildren() {
        return locationNode.getNavigationTree().getTree().hasChildren(this);
    }

    public LocationNode getLocationNode() {
        return locationNode;
    }

    public SessionDTO getSessionDTO() {
        return sessionDTO;
    }

    @Override
    public void onNodeClick(NodeClickEvent nodeClickEvent) {
        if (!ready) {
            final JaggerFacade facade = locationNode.getNavigationTree().getFacade();
            final long userAction = facade.nextUserAction();
            facade.showStatus(Status.WAITING, "Generating report for session " + sessionDTO.getSessionId() +
                                              " of " + locationNode.getLocationDTO().getName() + "...");
            ready = true;
            JaggerFacadeService.App.getInstance().getReport(facade.getUserId(),
                                                            locationNode.getLocationDTO().getName(), sessionDTO.getSessionId(), new AsyncCallback<ReportDTO>() {
                @Override
                public void onFailure(Throwable caught) {
                    ready = false;
                    if (facade.isLastUserAction(userAction)) {
                        facade.showStatus(Status.FAILURE, "Report generation for session " + sessionDTO.getSessionId() +
                                                          " of " + locationNode.getLocationDTO().getName() +
                                                          " is failed: " + caught.toString());
                    }
                }

                @Override
                public void onSuccess(ReportDTO result) {
                    if (facade.isLastUserAction(userAction)) {
                        facade.showStatus(Status.SUCCESS, "Report for session " + sessionDTO.getSessionId() +
                                                          " of " + locationNode.getLocationDTO().getName() +
                                                          " is successfully generated");
                    }
                    new ReportNode(SessionNode.this, result);
                }
            });
        }
    }

    public static SessionNode[] createSessionNodes(LocationNode locationNode, SessionDTO[] sessionDTOs) {
        SessionNode[] sessionNodes = new SessionNode[sessionDTOs.length];
        int i = 0;
        for (SessionDTO sessionDTO : sessionDTOs) {
            sessionNodes[i++] = new SessionNode(locationNode, sessionDTO);
        }
        return sessionNodes;
    }

    public static SessionNode[] createNewSessionNodes(LocationNode locationNode, SessionDTO[] sessionDTOs) {
        ArrayList<SessionNode> sessionNodes = new ArrayList<SessionNode>(sessionDTOs.length);
        int i = 0;
        for (SessionDTO sessionDTO : sessionDTOs) {
            SessionNode sessionNode = locationNode.getSessionNode(sessionDTO.getSessionId());
            if (sessionNode != null) {
                sessionNodes.add(sessionNode);
            } else {
                sessionNodes.add(new SessionNode(locationNode, sessionDTO));
            }
        }

        return sessionNodes.toArray(new SessionNode[sessionNodes.size()]);
    }
}
