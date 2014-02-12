package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.NodeInfoService;
import com.griddynamics.jagger.webclient.client.dto.NodeInfoPerSessionDto;
import com.griddynamics.jagger.webclient.client.dto.SessionDataDto;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.griddynamics.jagger.webclient.client.trends.TrendsPlace;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

import java.util.*;

public class NodesPanel extends Composite {

    interface NodesPanelUiBinder extends UiBinder<Widget, NodesPanel> {
    }

    private static NodesPanelUiBinder ourUiBinder = GWT.create(NodesPanelUiBinder.class);

    @UiField
    VerticalPanel pane;

    private NodesTablePanel nodesTablePanel;
    private Set<String > currentSessionIds = new HashSet<String>();
    private Label noDataLabel;
    private TextButton createButton;
    private VerticalPanel tableBackgroundPanel;
    private HorizontalPanel controlBackgroundPanel;
    private Image loadIndicatorImage;
    private TrendsPlace place;

    public NodesPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setControlBackgroundPanel();
        setTableBackgroundPanel();
        pane.add(controlBackgroundPanel);
        pane.add(tableBackgroundPanel);
        clear();
    }

    public void clear() {
        tableBackgroundPanel.clear();
        createButton.disable();

        currentSessionIds.clear();
    }

    public void updateSetup(Set<SessionDataDto> chosenSessions, TrendsPlace place) {
        // Update currently selected sessions
        for (SessionDataDto session : chosenSessions) {
            currentSessionIds.add(session.getSessionId());
        }
        if (currentSessionIds.size() > 0)
            createButton.enable();

        // Set place to handle exception window
        this.place = place;
    }

    private void setNoDataLabel() {
        noDataLabel = new Label("No info about nodes available");
        noDataLabel.getElement().getStyle().setFontSize(12, Style.Unit.PX);
        noDataLabel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
        noDataLabel.setHeight("100%");
    }

    private void setCreateButton() {
        createButton = new TextButton("Get node info");
        createButton.setPixelSize(60, 22);
        createButton.getElement().setMargins(new Margins(10, 10, 10, 10));
        createButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                nodeInfoFetcher.fetchNodeInfo(currentSessionIds);
            }
        });
    }
    private void setLoadIndicatorImage() {
        ImageResource imageResource = JaggerResources.INSTANCE.getLoadIndicator();
        loadIndicatorImage = new Image(imageResource);
        loadIndicatorImage.setVisible(false);
    }
    private void setControlBackgroundPanel() {
        controlBackgroundPanel = new HorizontalPanel();
        controlBackgroundPanel.setWidth("100%");

        setCreateButton();
        setLoadIndicatorImage();

        controlBackgroundPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        controlBackgroundPanel.add(createButton);
        controlBackgroundPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        controlBackgroundPanel.add(loadIndicatorImage);
    }
    private void setTableBackgroundPanel() {
        tableBackgroundPanel = new VerticalPanel();
        tableBackgroundPanel.setSpacing(10);

        setNoDataLabel();
    }

    private void createTables(List<NodeInfoPerSessionDto> nodeInfoPerSessionDtoList) {

        if (nodeInfoPerSessionDtoList.size() > 0) {
            //sort by sessionId
            SortedSet<NodeInfoPerSessionDto> sortedNodeInfoPerSessionDtoList = new TreeSet<NodeInfoPerSessionDto>(new Comparator<NodeInfoPerSessionDto>() {
                @Override
                public int compare(NodeInfoPerSessionDto o, NodeInfoPerSessionDto o2) {
                    return (Long.parseLong(o.getSessionId()) - Long.parseLong(o2.getSessionId())) > 0 ? 1 : -1;
                }
            });
            sortedNodeInfoPerSessionDtoList.addAll(nodeInfoPerSessionDtoList);

            // build tables
            tableBackgroundPanel.setVisible(false);
            for (NodeInfoPerSessionDto nodeInfoPerSessionDto : sortedNodeInfoPerSessionDtoList) {
                NodesTablePanel nodesTablePanel = new NodesTablePanel();
                nodesTablePanel.createTable(nodeInfoPerSessionDto.getSessionId(), nodeInfoPerSessionDto.getNodes());
                tableBackgroundPanel.add(nodesTablePanel);
            }
            tableBackgroundPanel.setVisible(true);
        }
        else {
            tableBackgroundPanel.add(noDataLabel);
        }

    }

    private void displayBeforeLoad() {
        tableBackgroundPanel.clear();
        createButton.disable();
        loadIndicatorImage.setVisible(true);
    }
    private void displayAfterLoad() {
        loadIndicatorImage.setVisible(false);
        createButton.enable();
    }

    private NodeInfoFetcher nodeInfoFetcher = new NodeInfoFetcher();

    private class NodeInfoFetcher
    {
        public void fetchNodeInfo(Set<String> sessionIds) {
            displayBeforeLoad();

            NodeInfoService.Async.getInstance().getNodeInfo(sessionIds, new AsyncCallback<List<NodeInfoPerSessionDto>>() {
                @Override
                public void onFailure(Throwable caught) {
                    caught.printStackTrace();
                    new ExceptionPanel(place, caught.getMessage());
                    displayAfterLoad();
                }

                @Override
                public void onSuccess(List<NodeInfoPerSessionDto> result) {
                    createTables(result);
                    displayAfterLoad();
                }
            });
        }
    }

}