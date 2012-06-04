package com.griddynamics.jagger.webclient.client;

import ca.nanometrics.gflot.client.DataPoint;
import ca.nanometrics.gflot.client.PlotModel;
import ca.nanometrics.gflot.client.SeriesHandler;
import ca.nanometrics.gflot.client.SimplePlot;
import ca.nanometrics.gflot.client.event.PlotHoverListener;
import ca.nanometrics.gflot.client.event.PlotItem;
import ca.nanometrics.gflot.client.event.PlotPosition;
import ca.nanometrics.gflot.client.jsni.Plot;
import ca.nanometrics.gflot.client.options.*;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.*;
import com.google.gwt.view.client.Range;
import com.griddynamics.jagger.webclient.client.dto.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/28/12
 */
public class Trends extends Composite {
    interface TrendsUiBinder extends UiBinder<Widget, Trends> {
    }

    private static TrendsUiBinder uiBinder = GWT.create(TrendsUiBinder.class);

    private static final String INSTRUCTIONS = "Point your mouse to a data point on the chart";
    private static final int MAX_PLOT_COUNT = 30;

    @UiField
    HTMLPanel plotPanel;

    @UiField(provided = true)
    DataGrid<SessionDataDto> sessionsDataGrid;

    @UiField(provided = true)
    SimplePager sessionsPager;

    @UiField(provided = true)
    CellTree taskDetailsTree;

    @UiField
    ScrollPanel scrollPanel;

    private FlowPanel loadIndicator;

    private SessionDataAsyncDataProvider dataProvider = new SessionDataAsyncDataProvider();

    public Trends() {
        setupTaskDetailsTree();
        setupDataGrid();
        setupPager();
        setupLoadIndicator();
        initWidget(uiBinder.createAndBindUi(this));
    }

    private SimplePlot createPlot() {
        PlotOptions plotOptions = new PlotOptions();
        plotOptions.setGlobalSeriesOptions(new GlobalSeriesOptions()
                .setLineSeriesOptions(new LineSeriesOptions().setLineWidth(1).setShow(true).setFill(0.1))
                .setPointsOptions(new PointsSeriesOptions().setRadius(2).setShow(true)).setShadowSize(0d));

        // Make the grid hoverable
        plotOptions.setGridOptions(new GridOptions().setHoverable(true));
        // create the plot
        SimplePlot plot = new SimplePlot(plotOptions);
        plot.setHeight(200);
        plot.setWidth("100%");

        final PopupPanel popup = new PopupPanel();
        final Label label = new Label();
        popup.add(label);

        // add hover listener
        plot.addHoverListener(new PlotHoverListener() {
            public void onPlotHover(Plot plot, PlotPosition position, PlotItem item) {
                if (item != null) {
                    String text = "x: " + item.getDataPoint().getX() + ", y: " + item.getDataPoint().getY();

                    label.setText(text);
                    popup.setPopupPosition(item.getPageX() + 10, item.getPageY() - 25);
                    popup.show();
                } else {
                    popup.hide();
                }
            }
        }, false);

        return plot;
    }

    private void setupDataGrid() {
        sessionsDataGrid = new DataGrid<SessionDataDto>();
        sessionsDataGrid.setPageSize(15);
        sessionsDataGrid.setEmptyTableWidget(new Label("No Sessions"));

        // Add a selection model so we can select cells.
        final SelectionModel<SessionDataDto> selectionModel = new SingleSelectionModel<SessionDataDto>(new ProvidesKey<SessionDataDto>() {
            @Override
            public Object getKey(SessionDataDto item) {
                return item.getSessionId();
            }
        });
        sessionsDataGrid.setSelectionModel(selectionModel, DefaultSelectionEventManager.<SessionDataDto>createCheckboxManager());

        selectionModel.addSelectionChangeHandler(new SessionSelectChangeHandler());

        // Checkbox column. This table will uses a checkbox column for selection.
        // Alternatively, you can call dataGrid.setSelectionEnabled(true) to enable mouse selection.
        Column<SessionDataDto, Boolean> checkColumn =
                new Column<SessionDataDto, Boolean>(new CheckboxCell(true, false)) {
                    @Override
                    public Boolean getValue(SessionDataDto object) {
                        // Get the value from the selection model.
                        return selectionModel.isSelected(object);
                    }
                };
        sessionsDataGrid.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        sessionsDataGrid.setColumnWidth(checkColumn, 40, Style.Unit.PX);

        Column nameColumn = new TextColumn<SessionDataDto>() {
            @Override
            public String getValue(SessionDataDto object) {
                return object.getName();
            }
        };
        sessionsDataGrid.addColumn(nameColumn, "Name");

        sessionsDataGrid.addColumn(new TextColumn<SessionDataDto>() {
            @Override
            public String getValue(SessionDataDto object) {
                return object.getStartDate().toString();
            }
        }, "Start Date");

        sessionsDataGrid.addColumn(new TextColumn<SessionDataDto>() {
            @Override
            public String getValue(SessionDataDto object) {
                return object.getEndDate().toString();
            }
        }, "End Date");

        dataProvider.addDataDisplay(sessionsDataGrid);
    }

    private void setupPager() {
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        sessionsPager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        sessionsPager.setDisplay(sessionsDataGrid);
    }

    private void setupTaskDetailsTree() {
        CellTree.Resources res = GWT.create(CellTree.BasicResources.class);
        final MultiSelectionModel<PlotNameDto> selectionModel = new MultiSelectionModel<PlotNameDto>();
        taskDetailsTree = new CellTree(new WorkloadTaskDetailsTreeViewModel(selectionModel), null, res);
        taskDetailsTree.addStyleName("task-details-tree");

        selectionModel.addSelectionChangeHandler(new TaskPlotSelectionChangedHandler());
    }

    private void setupLoadIndicator() {
        ImageResource imageResource = JaggerResources.INSTANCE.getLoadIndicator();
        Image image = new Image(imageResource);
        loadIndicator = new FlowPanel();
        loadIndicator.addStyleName("centered");
        loadIndicator.add(image);
    }

    private String generateId(PlotNameDto plotNameDto) {
        return "" + plotNameDto.getTaskId() + "_" + plotNameDto.getPlotName().replace(" ", "_");
    }

    private boolean isMaxPlotCountReached() {
        return plotPanel.getWidgetCount() >= MAX_PLOT_COUNT;
    }

    //==========Nested Classes

    /**
     * Fetches all sessions from server
     */
    private static class SessionDataAsyncDataProvider extends AsyncDataProvider<SessionDataDto> {
        @Override
        protected void onRangeChanged(HasData<SessionDataDto> display) {
            Range range = display.getVisibleRange();
            final int start = range.getStart();
            int end = start + range.getLength();

            SessionDataServiceAsync sessionDataService = SessionDataService.Async.getInstance();
            AsyncCallback<PagedSessionDataDto> callback = new AsyncCallback<PagedSessionDataDto>() {
                @Override
                public void onFailure(Throwable caught) {
//                    Window.alert("Error is occurred during server request processing (Session data fetching)");
                    Window.alert("Error is occurred during server request processing (Session data fetching)" + caught.getMessage());
                }

                @Override
                public void onSuccess(PagedSessionDataDto result) {
                    updateRowData(start, result.getSessionDataDtoList());
                    updateRowCount(result.getTotalSize(), true);
                }
            };
            sessionDataService.getAll(start, range.getLength(), callback);
        }
    }

    /**
     * Handles select session event
     */
    private class SessionSelectChangeHandler implements SelectionChangeEvent.Handler {
        @Override
        public void onSelectionChange(SelectionChangeEvent event) {
            // Currently selection model for sessions is a single selection model
            SessionDataDto selected = ((SingleSelectionModel<SessionDataDto>) event.getSource()).getSelectedObject();

            final WorkloadTaskDetailsTreeViewModel workloadTaskDetailsTreeViewModel = (WorkloadTaskDetailsTreeViewModel) taskDetailsTree.getTreeViewModel();
            final ListDataProvider<TaskDataDto> taskDataProvider = workloadTaskDetailsTreeViewModel.getTaskDataProvider();
            if (selected != null) {
                TaskDataService.Async.getInstance().getTaskDataForSession(selected.getSessionId(), new AsyncCallback<List<TaskDataDto>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Error is occurred during server request processing (Task data fetching)");
                    }

                    @Override
                    public void onSuccess(List<TaskDataDto> result) {
                        // Populate task first level tree with server data
                        taskDataProvider.getList().clear();
                        taskDataProvider.getList().addAll(result);

                        // Populate available plots tree level for each task for selected session
                        for (TaskDataDto taskDataDto : result) {
                            final ListDataProvider<PlotNameDto> plotNameDataProvider = ((WorkloadTaskDetailsTreeViewModel)
                                    taskDetailsTree.getTreeViewModel()).getPlotNameDataProvider(taskDataDto);

                            PlotProviderService.Async.getInstance().getPlotListForTask(taskDataDto.getId(), new AsyncCallback<List<PlotNameDto>>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                    Window.alert("Error is occurred during server request processing (Plot names for task fetching)");
                                }

                                @Override
                                public void onSuccess(List<PlotNameDto> result) {
                                    plotNameDataProvider.getList().clear();
                                    plotNameDataProvider.getList().addAll(result);

                                    // Close all tree nodes when new session is selected
                                    int childCount = taskDetailsTree.getRootTreeNode().getChildCount();
                                    for (int i = 0; i < childCount; i++) {
                                        taskDetailsTree.getRootTreeNode().setChildOpen(i, false);
                                    }
                                }
                            });
                        }
                    }
                });
            } else {
                // If no sessions are selected, clear plot display, clear task tree, clear plot selection model
                plotPanel.clear();
                taskDataProvider.getList().clear();
                taskDataProvider.getList().add(WorkloadTaskDetailsTreeViewModel.getNoTasksDummyNode());

                final MultiSelectionModel<PlotNameDto> plotNameSelectionModel = (MultiSelectionModel<PlotNameDto>) workloadTaskDetailsTreeViewModel.getSelectionModel();
                plotNameSelectionModel.clear();
            }
        }
    }

    /**
     * Handles specific plot of task selection
     */
    private class TaskPlotSelectionChangedHandler implements SelectionChangeEvent.Handler {
        @Override
        public void onSelectionChange(SelectionChangeEvent event) {
            Set<PlotNameDto> selected = ((MultiSelectionModel<PlotNameDto>) event.getSource()).getSelectedSet();

            if (!selected.isEmpty()) {
                // Generate all id of plots which should be displayed
                Set<String> selectedTaskIds = new HashSet<String>();
                for (PlotNameDto plotNameDto : selected) {
                    selectedTaskIds.add(generateId(plotNameDto));
                }

                // Remove plots from display which were unchecked
                for (int i=0; i<plotPanel.getWidgetCount(); i++) {
                    Widget widget = plotPanel.getWidget(i);
                    if (selectedTaskIds.contains(widget.getElement().getId())) {
                        continue;
                    }
                    // Remove plot
                    plotPanel.remove(i);
                }

                // Creating plots and displaying they
                for (final PlotNameDto plotNameDto : selected) {
                    if (isMaxPlotCountReached()) {
                        Window.alert("You are reached max count of plot on display");
                        break;
                    }

                    // Generate DOM id for plot
                    final String id = generateId(plotNameDto);

                    // If plot has already displayed, then pass it
                    if (plotPanel.getElementById(id) != null) {
                        continue;
                    }

                    plotPanel.add(loadIndicator);
                    scrollPanel.scrollToBottom();
                    final int loadingId = plotPanel.getWidgetCount()-1;
                    // Invoke remote service for plot data retrieving
                    PlotProviderService.Async.getInstance().getPlotData(plotNameDto.getTaskId(), plotNameDto.getPlotName(), new AsyncCallback<PlotSeriesDto>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            plotPanel.remove(loadingId);

                            Window.alert("Error is occurred during server request processing (Throughput data fetching)");
                        }

                        @Override
                        public void onSuccess(PlotSeriesDto result) {
                            plotPanel.remove(loadingId);

                            SimplePlot plot = createPlot();
                            PlotModel plotModel = plot.getModel();

                            for (PlotDatasetDto plotDatasetDto: result.getPlotSeries()) {
                                SeriesHandler handler = plotModel.addSeries(plotDatasetDto.getLegend(), plotDatasetDto.getColor());

                                // Populate plot with data
                                for (PointDto pointDto : plotDatasetDto.getPlotData()) {
                                    handler.add(new DataPoint(pointDto.getX(), pointDto.getY()));
                                }
                            }

                            // Add X axis label
                            Label xLabel = new Label(result.getXAxisLabel());
                            xLabel.addStyleName("x-axis-label");

                            Label plotHeader = new Label(result.getPlotHeader());
                            plotHeader.addStyleName("plot-header");

                            Label plotLegend = new Label("PLOT LEGEND");
                            plotLegend.addStyleName("plot-legend");

                            VerticalPanel vp = new VerticalPanel();
                            vp.getElement().setId(id);
                            vp.setWidth("100%");

                            vp.add(plotHeader);
                            vp.add(plot);
                            vp.add(xLabel);
                            // Will be added if there is need it
//                            vp.add(plotLegend);

                            plotPanel.add(vp);

                            // Redraw plot
                            plot.redraw();
                        }
                    });
                }
            } else {
                // Clear display because of no checked plots
                plotPanel.clear();
            }
        }
    }

}