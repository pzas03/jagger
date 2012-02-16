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
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.NodeClickEvent;
import com.smartgwt.client.widgets.tree.events.NodeClickHandler;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * User: dkotlyarov
 */
public class FrameNode extends TreeNode implements NodeClickHandler {
    private final ReportNode reportNode;
    private final FrameDTO frameDTO;
    private final FrameNode parentFrame;
    private final FrameNode[] childFrames;

    public FrameNode(ReportNode reportNode, FrameDTO frameDTO) {
        this.reportNode = reportNode;
        this.frameDTO = frameDTO;
        this.parentFrame = null;

        setAttribute("id", reportNode.getAttribute("id") + ", Frame: " + frameDTO.getIndex());
        setAttribute("name", frameDTO.getName());
        reportNode.getSessionNode().getLocationNode().getNavigationTree().getTree().add(this, reportNode);

        this.childFrames = createFrameNodes(this, frameDTO.getChildFrames());
    }

    public FrameNode(FrameNode frameNode, FrameDTO frameDTO) {
        this.reportNode = frameNode.reportNode;
        this.frameDTO = frameDTO;
        this.parentFrame = frameNode;

        setAttribute("id", frameNode.getAttribute("id") + ", Frame: " + frameDTO.getIndex());
        setAttribute("name", frameDTO.getName());
        reportNode.getSessionNode().getLocationNode().getNavigationTree().getTree().add(this, frameNode);

        this.childFrames = createFrameNodes(this, frameDTO.getChildFrames());
    }

    public void delete() {
        reportNode.getSessionNode().getLocationNode().getNavigationTree().getTree().remove(this);
    }

    public boolean hasChildren() {
        return reportNode.getSessionNode().getLocationNode().getNavigationTree().getTree().hasChildren(this);
    }

    public ReportNode getReportNode() {
        return reportNode;
    }

    public FrameDTO getFrameDTO() {
        return frameDTO;
    }

    public FrameNode getParentFrame() {
        return parentFrame;
    }

    public FrameNode[] getChildFrames() {
        return childFrames;
    }

    public ArrayList<Integer> getPath() {
        LinkedList<Integer> path = new LinkedList<Integer>();
        for (FrameNode frameNode = this; frameNode != null; frameNode = frameNode.parentFrame) {
            path.addFirst(frameNode.getFrameDTO().getIndex());
        }
        return new ArrayList<Integer>(path);
    }

    @Override
    public void onNodeClick(NodeClickEvent nodeClickEvent) {
        final NavigationTree navigationTree = reportNode.getSessionNode().getLocationNode().getNavigationTree();
        final JaggerFacade facade = navigationTree.getFacade();
        final long userAction = facade.nextUserAction();

        facade.showStatus(Status.WAITING, "Assembling subreport...");

        ListGridRecord[] records = navigationTree.getSelectedRecords();
        ArrayList<ReportRequestDTO> reportRequestDTOs = new ArrayList<ReportRequestDTO>(records.length);
        String sessionId = null;
        for (ListGridRecord record : records) {
            if (record instanceof FrameNode) {
                FrameNode frameNode = (FrameNode) record;
                String currentSessionId = frameNode.getReportNode().getSessionNode().getSessionDTO().getSessionId();
                if (!currentSessionId.equals(sessionId)) {
                    reportRequestDTOs.add(new ReportRequestDTO(currentSessionId));
                    sessionId = currentSessionId;
                }
                ReportRequestDTO reportRequestDTO = reportRequestDTOs.get(reportRequestDTOs.size() - 1);
                reportRequestDTO.getFramePaths().add(frameNode.getPath());
            }
        }

        JaggerFacadeService.App.getInstance().exportReport(facade.getUserId(),
                                                           reportNode.getSessionNode().getLocationNode().getLocationDTO().getName(),
                                                           reportRequestDTOs.toArray(new ReportRequestDTO[reportRequestDTOs.size()]),
                                                           new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                if (facade.isLastUserAction(userAction)) {
                    facade.showStatus(Status.FAILURE, "Subreport assembling is failed: " + caught.toString());
                }
            }

            @Override
            public void onSuccess(String result) {
                if (facade.isLastUserAction(userAction)) {
                    facade.showStatus(Status.SUCCESS, "Subreport is successfully assembled");
                }

                String hostPageBaseURL = GWT.getHostPageBaseURL();
                Layout layout = navigationTree.getFacade().getViewLayout();
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

    public static FrameNode[] createFrameNodes(ReportNode reportNode, FrameDTO[] frameDTOs) {
        FrameNode[] frameNodes = new FrameNode[frameDTOs.length];
        int i = 0;
        for (FrameDTO frameDTO : frameDTOs) {
            frameNodes[i++] = new FrameNode(reportNode, frameDTO);
        }
        return frameNodes;
    }

    public static FrameNode[] createFrameNodes(FrameNode frameNode, FrameDTO[] frameDTOs) {
        FrameNode[] frameNodes = new FrameNode[frameDTOs.length];
        int i = 0;
        for (FrameDTO frameDTO : frameDTOs) {
            frameNodes[i++] = new FrameNode(frameNode, frameDTO);
        }
        return frameNodes;
    }
}
