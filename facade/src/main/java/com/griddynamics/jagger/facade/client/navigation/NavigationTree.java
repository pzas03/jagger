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
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.NodeClickEvent;
import com.smartgwt.client.widgets.tree.events.NodeClickHandler;

import javax.persistence.criteria.Root;

/**
 * User: dkotlyarov
 */
public class NavigationTree extends TreeGrid {
    private final JaggerFacade facade;
    private final RootNode rootNode = new RootNode();

    public NavigationTree(JaggerFacade facade) {
        this.facade = facade;

        setAlign(Alignment.CENTER);
        setOverflow(Overflow.HIDDEN);
        setWidth("30%");
        setShowHeader(false);
        setShowResizeBar(true);
        setSelectionType(SelectionStyle.MULTIPLE);
        setCanEdit(false);
        setCanReparentNodes(false);
        setShowConnectors(true);
        setCanFreezeFields(true);
        setAutoFetchData(true);
        setLoadDataOnDemand(false);

        Tree tree = new Tree();
        tree.setModelType(TreeModelType.CHILDREN);
        tree.setIdField("id");
        tree.setNameProperty("name");
        tree.setData(new RootNode[] {rootNode});
        setData(tree);

        JaggerFacadeService.App.getInstance().getLocations(new AsyncCallback<LocationDTO[]>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(LocationDTO[] locationDTOs) {
                LocationNode.createLocationNodes(NavigationTree.this, locationDTOs);
                addNodeClickHandler(new NodeClickHandler() {
                    @Override
                    public void onNodeClick(NodeClickEvent nodeClickEvent) {
                        TreeNode treeNode = nodeClickEvent.getNode();
                        if (treeNode instanceof NodeClickHandler) {
                            ((NodeClickHandler) treeNode).onNodeClick(nodeClickEvent);
                        }
                    }
                });
            }
        });
    }

    public JaggerFacade getFacade() {
        return facade;
    }

    public RootNode getRootNode() {
        return rootNode;
    }
}
