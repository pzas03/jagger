package com.griddynamics.jagger.webclient.client;

import com.google.gwt.cell.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class WorkloadTaskDetailsTreeViewModel implements TreeViewModel {

    private ListDataProvider<TaskDataDto> taskDataDataProvider;
    private final SelectionModel<PlotNameDto> selectionModel;
    private final Cell<PlotNameDto> plotNameCell;
    private final DefaultSelectionEventManager<PlotNameDto> selectionManager =
            DefaultSelectionEventManager.createCheckboxManager();

    public WorkloadTaskDetailsTreeViewModel(final SelectionModel<PlotNameDto> selectionModel) {
        this.selectionModel = selectionModel;

        ArrayList<TaskDataDto> taskDataDtoList = new ArrayList<TaskDataDto>();
        taskDataDtoList.add(new TaskDataDto(1, "1", 1, "task-1", "Successful"));
        taskDataDtoList.add(new TaskDataDto(1, "1", 2, "task-2", "Successful"));
        taskDataDataProvider = new ListDataProvider<TaskDataDto>(taskDataDtoList);

        // Construct a composite cell for plots that includes a checkbox.
        List<HasCell<PlotNameDto, ?>> hasCells = new ArrayList<HasCell<PlotNameDto, ?>>();
        hasCells.add(new HasCell<PlotNameDto, Boolean>() {
            private CheckboxCell cell = new CheckboxCell(true, false);

            @Override
            public Cell<Boolean> getCell() {
                return cell;
            }

            @Override
            public FieldUpdater<PlotNameDto, Boolean> getFieldUpdater() {
                return null;
            }

            @Override
            public Boolean getValue(PlotNameDto object) {
                return selectionModel.isSelected(object);
            }
        });
        hasCells.add(new HasCell<PlotNameDto, PlotNameDto>() {
            private PlotNameCell cell = new PlotNameCell();

            @Override
            public Cell<PlotNameDto> getCell() {
                return cell;
            }

            @Override
            public FieldUpdater<PlotNameDto, PlotNameDto> getFieldUpdater() {
                return null;
            }

            @Override
            public PlotNameDto getValue(PlotNameDto object) {
                return object;
            }
        });
        plotNameCell = new CompositeCell<PlotNameDto>(hasCells) {
            @Override
            public void render(Context context, PlotNameDto value, SafeHtmlBuilder sb) {
                sb.appendHtmlConstant("<table><tbody><tr>");
                super.render(context, value, sb);
                sb.appendHtmlConstant("</tr></tbody></table>");
            }

            @Override
            protected Element getContainerElement(Element parent) {
                // Return the first TR element in the table.
                return parent.getFirstChildElement().getFirstChildElement().getFirstChildElement();
            }

            @Override
            protected <X> void render(Context context, PlotNameDto value,
                                      SafeHtmlBuilder sb, HasCell<PlotNameDto, X> hasCell) {
                Cell<X> cell = hasCell.getCell();
                sb.appendHtmlConstant("<td>");
                cell.render(context, hasCell.getValue(value), sb);
                sb.appendHtmlConstant("</td>");
            }
        };
    }

    @Override
    public <T> NodeInfo<?> getNodeInfo(T value) {
        if (value == null) {
            return new DefaultNodeInfo<TaskDataDto>(taskDataDataProvider, new TaskDataCell());
        } else if (value instanceof TaskDataDto) {
            return new DefaultNodeInfo<PlotNameDto>(
                    new ListDataProvider<PlotNameDto>(Arrays.asList(new PlotNameDto(1L, PlotNameDto.LATENCY_PLOT), new PlotNameDto(2L, PlotNameDto.THROUGHPUT_PLOT), new PlotNameDto(1L, PlotNameDto.THROUGHPUT_PLOT))),
                    plotNameCell,
                    selectionModel,
                    selectionManager,
                    null);
        }

        String type = value.getClass().getName();
        throw new IllegalArgumentException("Unsupported object type: " + type);
    }

    @Override
    public boolean isLeaf(Object value) {
        return value instanceof PlotNameDto;
    }


    private static class TaskDataCell extends AbstractCell<TaskDataDto> {
        @Override
        public void render(Context context, TaskDataDto value, SafeHtmlBuilder sb) {
            if (value != null) {
                sb.appendEscaped(value.getTaskName());
            }
        }
    }

    private static class PlotNameCell extends AbstractCell<PlotNameDto> {
        @Override
        public void render(Context context, PlotNameDto value, SafeHtmlBuilder sb) {
            if (value != null) {
                sb.appendEscaped(value.getPlotName());
            }
        }
    }

}
