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

import com.google.gwt.core.client.GWT;
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

/**
 * User: dkotlyarov
 */
public class ReportNode extends TreeNode implements NodeClickHandler {
    private final SessionNode sessionNode;
    private final ReportDTO reportDTO;
    private boolean ready = false;

    public ReportNode(SessionNode sessionNode, ReportDTO reportDTO) {
        this.sessionNode = sessionNode;
        this.reportDTO = reportDTO;

        setAttribute("id", sessionNode.getAttribute("id") + ", Report");
        setAttribute("name", "Report");
        sessionNode.getLocationNode().getNavigationTree().getTree().add(this, sessionNode);
    }

    public void delete() {
        sessionNode.getLocationNode().getNavigationTree().getTree().remove(this);
    }

    public boolean hasChildren() {
        return sessionNode.getLocationNode().getNavigationTree().getTree().hasChildren(this);
    }

    public SessionNode getSessionNode() {
        return sessionNode;
    }

    public ReportDTO getReportDTO() {
        return reportDTO;
    }

    @Override
    public void onNodeClick(NodeClickEvent nodeClickEvent) {
        if (!ready) {
            FrameNode.createFrameNodes(this, reportDTO.getFrames());
            ready = true;
        }

        final JaggerFacade facade = sessionNode.getLocationNode().getNavigationTree().getFacade();
        final long userAction = facade.nextUserAction();
        facade.showStatus(Status.WAITING, "Assembling report for session " + sessionNode.getSessionDTO().getSessionId() +
                                          " of " + sessionNode.getLocationNode().getLocationDTO().getName() + "...");
        JaggerFacadeService.App.getInstance().exportReport(facade.getUserId(),
                                                           sessionNode.getLocationNode().getLocationDTO().getName(), sessionNode.getSessionDTO().getSessionId(), new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                if (facade.isLastUserAction(userAction)) {
                    facade.showStatus(Status.FAILURE, "Report assembling for session " + sessionNode.getSessionDTO().getSessionId() +
                                                      " of " + sessionNode.getLocationNode().getLocationDTO().getName() +
                                                      " is failed: " + caught.toString());
                }
            }

            @Override
            public void onSuccess(String result) {
                if (facade.isLastUserAction(userAction)) {
                    facade.showStatus(Status.SUCCESS, "Report for session " + sessionNode.getSessionDTO().getSessionId() +
                                                      " of " + sessionNode.getLocationNode().getLocationDTO().getName() +
                                                      " is successfully assembled");
                }

                String hostPageBaseURL = GWT.getHostPageBaseURL();
                Layout layout = sessionNode.getLocationNode().getNavigationTree().getFacade().getViewLayout();
                HTMLPane htmlPane = new HTMLPane();
                htmlPane.setShowEdges(true);
                htmlPane.setContentsURL(hostPageBaseURL + result);
                htmlPane.setContentsType(ContentsType.PAGE);
                layout.setMembers();
                layout.addMember(htmlPane);
                layout.redraw();
            }
        });
    }
}
