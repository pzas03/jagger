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
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.*;
import com.google.gwt.view.client.Range;
import com.griddynamics.jagger.webclient.client.dto.PagedSessionDataDto;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.SessionDataDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/28/12
 */
public class Trends extends Composite {
    interface TrendsUiBinder extends UiBinder<Widget, Trends> {
    }

    private static TrendsUiBinder uiBinder = GWT.create(TrendsUiBinder.class);

    private static final String INSTRUCTIONS = "Point your mouse to a data point on the chart";

    //Plot
    @UiField(provided = true)
    SimplePlot plot;

    @UiField(provided = true)
    Label hoverPoint = new Label(INSTRUCTIONS);

    @UiField
    Label cursorPosition;

    @UiField(provided = true)
    DataGrid<SessionDataDto> sessionsDataGrid;

    @UiField(provided = true)
    SimplePager sessionsPager;

    @UiField(provided = true)
    CellTree taskDetailsTree;

    private SessionDataAsyncDataProvider dataProvider = new SessionDataAsyncDataProvider();

    public Trends() {
        createPlot();
        setupTaskDetailsTree();
        setupDataGrid();
        setupPager();
        initWidget(uiBinder.createAndBindUi(this));
    }

    private void createPlot() {
        PlotModel model = new PlotModel();
        PlotOptions plotOptions = new PlotOptions();
        plotOptions.setGlobalSeriesOptions(new GlobalSeriesOptions()
                .setLineSeriesOptions(new LineSeriesOptions().setLineWidth(1).setShow(true))
                .setPointsOptions(new PointsSeriesOptions().setRadius(2).setShow(true)).setShadowSize(0d));

        // Make the grid hoverable
        plotOptions.setGridOptions(new GridOptions().setHoverable(true));

        // create a series
        SeriesHandler handler = model.addSeries("Ottawa's Month Temperatures (Daily Average in &deg;C)", "#007f00");

        // add data
        handler.add(new DataPoint(1, -10.5));
        handler.add(new DataPoint(2, -8.6));
        handler.add(new DataPoint(3, -2.4));
        handler.add(new DataPoint(4, 6));
        handler.add(new DataPoint(5, 13.6));
        handler.add(new DataPoint(6, 18.4));
        handler.add(new DataPoint(7, 21));
        handler.add(new DataPoint(8, 19.7));
        handler.add(new DataPoint(9, 14.7));
        handler.add(new DataPoint(10, 8.2));
        handler.add(new DataPoint(11, 1.5));
        handler.add(new DataPoint(12, -6.6));
        handler.add(new DataPoint(14, -6.6));
        handler.add(new DataPoint(16, -6.6));
        handler.add(new DataPoint(17, -6.6));
        handler.add(new DataPoint(18, -6.6));
        handler.add(new DataPoint(19, -6.6));
        handler.add(new DataPoint(22, -6.6));
        handler.add(new DataPoint(23, -6.6));
        handler.add(new DataPoint(24, -6.6));
        handler.add(new DataPoint(25, -6.6));
        handler.add(new DataPoint(27, -6.6));
        handler.add(new DataPoint(29, -6.6));
        handler.add(new DataPoint(35, -6.6));

        // create the plot
        plot = new SimplePlot(model, plotOptions);

        final PopupPanel popup = new PopupPanel();
        final Label label = new Label();
        popup.add(label);

        // add hover listener
        plot.addHoverListener(new PlotHoverListener() {
            public void onPlotHover(Plot plot, PlotPosition position, PlotItem item) {
                if (item != null) {
                    String text = "x: " + item.getDataPoint().getX() + ", y: " + item.getDataPoint().getY();

                    hoverPoint.setText(text);

                    label.setText(text);
                    popup.setPopupPosition(item.getPageX() + 10, item.getPageY() - 25);
                    popup.show();
                } else {
                    hoverPoint.setText(INSTRUCTIONS);
                    popup.hide();
                }
            }
        }, false);
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
                    Window.alert("Error is occurred during server request processing (Session data fetching)");
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
            SessionDataDto selected = ((SingleSelectionModel<SessionDataDto>) event.getSource()).getSelectedObject();

            WorkloadTaskDetailsTreeViewModel workloadTaskDetailsTreeViewModel = (WorkloadTaskDetailsTreeViewModel) taskDetailsTree.getTreeViewModel();
            final ListDataProvider<TaskDataDto> taskDataProvider = workloadTaskDetailsTreeViewModel.getTaskDataProvider();
            if (selected != null) {
                TaskDataService.Async.getInstance().getTaskDataForSession(selected.getSessionId(), new AsyncCallback<List<TaskDataDto>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert("Error is occurred during server request processing (Task data fetching)");
                    }

                    @Override
                    public void onSuccess(List<TaskDataDto> result) {
                        taskDataProvider.getList().clear();
                        taskDataProvider.getList().addAll(result);

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
                                }
                            });
                        }
                    }
                });
            } else {
                taskDataProvider.getList().clear();
                taskDataProvider.getList().add(WorkloadTaskDetailsTreeViewModel.getNoTasksDummyNode());
            }
        }
    }

}