package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.JsArray;
import com.googlecode.gflot.client.DataPoint;
import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.SeriesHandler;
import com.googlecode.gflot.client.SimplePlot;
import com.griddynamics.jagger.dbapi.dto.PlotSingleDto;
import com.griddynamics.jagger.dbapi.dto.PointDto;
import com.griddynamics.jagger.dbapi.model.LegendNode;
import com.griddynamics.jagger.webclient.client.components.control.LegendNodeCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

import java.util.List;

/**
 * Implementation of AbstractTree that represents interactive legend as tree.
 */
public class LegendTree extends AbstractTree<LegendNode, LegendNode> {

    /**
     * Plot that would controlled with Legend tree
     */
    private SimplePlot plot;

    /**
     * null if it is metric, not null if it is trend */
    // todo : JFG-803 simplify trends plotting mechanism
    private List<Integer> trendSessionIds;

    /**
     * Plots panel where plot is situated
     */
    private PlotsPanel plotsPanel;


    private final static ValueProvider<LegendNode, LegendNode> VALUE_PROVIDER =  new ValueProvider<LegendNode, LegendNode>() {

        @Override
        public LegendNode getValue(LegendNode object) {
            return object;
        }

        @Override
        public void setValue(LegendNode object, LegendNode value) {
            object.setDisplayName(value.getDisplayName());
            object.setMetricNameDtoList(value.getMetricNameDtoList());
            object.setId(value.getId());
            object.setLine(value.getLine());
        }

        @Override
        public String getPath() {
            return "legend";
        }
    };

    /**
     * Constructor matches super class
     *
     * @param trendSessionIds - set null if it is metric, sorted list of session ids if it is trend
     */
    public LegendTree(SimplePlot plot, PlotsPanel plotsPanel, List<Integer> trendSessionIds) {
        super(
                new TreeStore<LegendNode>(new ModelKeyProvider<LegendNode>() {
                    @Override
                    public String getKey(LegendNode item) {
                        return item.getId();
                    }
                }),
                VALUE_PROVIDER);
        this.plot = plot;
        this.plotsPanel = plotsPanel;
        this.trendSessionIds = trendSessionIds;

        this.setAutoExpand(true);
        this.setCell(LegendNodeCell.getInstance());
        this.setSelectionModel(null);

        // register tip manager for tree
        QuickTip qt = new QuickTip(this);
        qt.setShadow(false);
    }

    @Override
    protected void check(LegendNode item, CheckState state) {
        noRedrawCheck(item, state);
        redrawPlot();
    }

    /**
     * Adds or removes lines without redrawing plot. Changes can't be seen.
     * @param item chosen item
     * @param state check state
     */
    private void noRedrawCheck(LegendNode item, CheckState state) {
        PlotSingleDto plotSingleDto = item.getLine();

        if (plotSingleDto != null) {

            if (state == CheckState.CHECKED) {

                Series series = Series.create().setId(item.getId()).setColor(plotSingleDto.getColor()).setLabel(plotSingleDto.getLegend());
                SeriesHandler sh = plot.getModel().addSeries(series);
                for (PointDto point: plotSingleDto.getPlotData()) {
                    if (trendSessionIds != null) {
                        // it is trend
                        sh.add(DataPoint.of(trendSessionIds.indexOf((int) point.getX()), point.getY()));
                    } else {
                        sh.add(DataPoint.of(point.getX(), point.getY()));
                    }
                }

            } else if (state == CheckState.UNCHECKED) {

                // remove curve from view
                JsArray<Series> seriesArray = plot.getModel().getSeries();
                int k;
                for (k = 0; k < seriesArray.length(); k++) {
                    Series curSeries = seriesArray.get(k);
                    // label used as id
                    if (curSeries.getId().equals(item.getId())) {
                        // found
                        break;
                    }
                }
                if (k < seriesArray.length()) {
                    plot.getModel().removeSeries(k);
                }
            }
        } else {
            for (LegendNode child : store.getAllChildren(item)) {
                noRedrawCheck(child, state);
            }
        }
    }

    /**
     * Redraw plot with specific axis ranges
     */
    private void redrawPlot() {
        if (!plotsPanel.isEmpty()) {
            double minXVisible = plotsPanel.getMinXAxisVisibleValue();
            double maxXVisible = plotsPanel.getMaxXAxisVisibleValue();

            if (plot.isAttached()) {
                double minYVisible = plot.getAxes().getY().getMinimumValue();
                double maxYVisible = plot.getAxes().getY().getMaximumValue();

                // save y axis range for plot from very start
                plot.getOptions().getYAxisOptions().setMinimum(minYVisible).setMaximum(maxYVisible);
            }

            // set x axis in range as all other plots
            plot.getOptions().getXAxisOptions().setMinimum(minXVisible).setMaximum(maxXVisible);
            plot.redraw();
        }
    }
}
