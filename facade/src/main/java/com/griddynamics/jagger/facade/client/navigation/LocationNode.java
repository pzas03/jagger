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
import com.griddynamics.jagger.facade.client.Status;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.NodeClickEvent;
import com.smartgwt.client.widgets.tree.events.NodeClickHandler;

/**
 * User: dkotlyarov
 */
public class LocationNode extends TreeNode implements NodeClickHandler {
    private final NavigationTree navigationTree;
    private final LocationDTO locationDTO;
    private boolean ready = false;
    private final Timer timer;

    public LocationNode(NavigationTree navigationTree, final LocationDTO locationDTO) {
        this.navigationTree = navigationTree;
        this.locationDTO = locationDTO;

        setAttribute("id", "Location: " + locationDTO.getName());
        setAttribute("name", locationDTO.getName());
        navigationTree.getTree().add(this, navigationTree.getRootNode());

        final JaggerFacade facade = navigationTree.getFacade();

        this.timer = new Timer() {
            @Override
            public void run() {
                JaggerFacadeService.App.getInstance().getSessionCount(facade.getUserId(),
                                                                      locationDTO.getName(), new AsyncCallback<Integer>() {
                    @Override
                    public void onFailure(Throwable caught) {
                    }

                    @Override
                    public void onSuccess(Integer count) {
                        if (count > getSessionNodeCount()) {
                            JaggerFacadeService.App.getInstance().getSessions(facade.getUserId(),
                                                                              locationDTO.getName(), new AsyncCallback<SessionDTO[]>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                }

                                @Override
                                public void onSuccess(SessionDTO[] sessionDTOs) {
                                    SessionNode.createNewSessionNodes(LocationNode.this, sessionDTOs);

                                    Canvas[] children = getNavigationTree().getFacade().getViewLayout().getChildren();
                                    if ((children.length > 0) && (children[0] instanceof LocationForm)) {
                                        createForm();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        };
        timer.scheduleRepeating(5000);
    }

    public void delete() {
        navigationTree.getTree().remove(this);
    }

    public boolean hasChildren() {
        return navigationTree.getTree().hasChildren(this);
    }

    public int getSessionNodeCount() {
        return getNavigationTree().getTree().getChildren(this).length;
    }

    public SessionNode getSessionNode(String sessionId) {
        return (SessionNode) getNavigationTree().getTree().findById(getAttribute("id") + ", Session: " + sessionId);
    }

    public NavigationTree getNavigationTree() {
        return navigationTree;
    }

    public LocationDTO getLocationDTO() {
        return locationDTO;
    }

    @Override
    public void onNodeClick(NodeClickEvent nodeClickEvent) {
        if (!ready) {
            final JaggerFacade facade = getNavigationTree().getFacade();
            final long userAction = facade.nextUserAction();
            facade.showStatus(Status.WAITING, "Loading sessions for " + locationDTO.getName() + "...");
            ready = true;
            JaggerFacadeService.App.getInstance().getSessions(facade.getUserId(),
                                                              locationDTO.getName(), new AsyncCallback<SessionDTO[]>() {
                @Override
                public void onFailure(Throwable caught) {
                    ready = false;
                    if (facade.isLastUserAction(userAction)) {
                        facade.showStatus(Status.FAILURE, "Sessions loading for " + locationDTO.getName() + " is failed: " + caught.toString());
                    }

                    createForm();
                }

                @Override
                public void onSuccess(SessionDTO[] sessionDTOs) {
                    if (facade.isLastUserAction(userAction)) {
                        facade.showStatus(Status.SUCCESS, "Sessions for " + locationDTO.getName() + " are successfully loaded");
                    }
                    SessionNode.createSessionNodes(LocationNode.this, sessionDTOs);

                    createForm();
                }
            });
        } else {
            createForm();
        }
    }

    public LocationForm createForm() {
        LocationForm form = new LocationForm(getNavigationTree().getFacade().getViewLayout(), this);
        return form;
    }

    public static LocationNode[] createLocationNodes(NavigationTree navigationTree, LocationDTO[] locationDTOs) {
        LocationNode[] locationNodes = new LocationNode[locationDTOs.length];
        int i = 0;
        for (LocationDTO locationDTO : locationDTOs) {
            locationNodes[i++] = new LocationNode(navigationTree, locationDTO);
        }
        return locationNodes;
    }
}
