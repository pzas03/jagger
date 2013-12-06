package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiConstructor;
import com.griddynamics.jagger.webclient.client.components.control.CheckHandlerMap;
import com.griddynamics.jagger.webclient.client.components.control.model.*;
import com.griddynamics.jagger.webclient.client.dto.MetricNameDto;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.event.*;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Extension of com.sencha.gxt.widget.core.client.tree.Tree allows to disable tree items.
 * + no icons.
 *
 *
 * @param <C> cell data type
 */
public class NoIconTree <C> extends Tree <SimpleNode, C> {


    /**
     * boolean disabled tree or not
     * uses for canceling events
     */
    private boolean disabled;


    /**
     * Model helps to fetch all data at once
     */
    private RootNode rootNode;

    public RootNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(RootNode rootNode) {
        this.rootNode = rootNode;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    {

        this.addBeforeExpandHandler(new BeforeExpandItemEvent.BeforeExpandItemHandler<SimpleNode>() {
            @Override
            public void onBeforeExpand(BeforeExpandItemEvent<SimpleNode> event) {
                if (disabled)
                    event.setCancelled(true);
            }
        });

        this.addBeforeCollapseHandler(new BeforeCollapseItemEvent.BeforeCollapseItemHandler<SimpleNode>() {
            @Override
            public void onBeforeCollapse(BeforeCollapseItemEvent<SimpleNode> event) {
                if (disabled)
                    event.setCancelled(true);
            }
        });

        this.addBeforeCheckChangeHandler(new BeforeCheckChangeEvent.BeforeCheckChangeHandler<SimpleNode>() {
            @Override
            public void onBeforeCheckChange(BeforeCheckChangeEvent<SimpleNode> event) {
                if (disabled)
                    event.setCancelled(true);
            }
        });

        this.addCheckChangeHandler(new CheckChangeEvent.CheckChangeHandler<SimpleNode>() {
            @Override
            public void onCheckChange(CheckChangeEvent<SimpleNode> event) {
                // testing
                Info.display("treeDemo", event.getItem() + "");

                tree.disableEvents();
                    check(event.getItem(), event.getChecked());
            }

            private void check(SimpleNode item, CheckState state) {
                checkSubTree(item, state);
                if (state.equals(CheckState.CHECKED)) {
                    checkParent(item);
                    tree.setExpanded(item, true, true);
                } else {
                    unCheckParent(item);
                }

                tree.enableEvents();
                NoIconTree.this.disable();
                CheckHandlerMap.getHandler(item.getClass()).onCheckChange(new CheckChangeEvent(item, state));
            }

            private Tree<SimpleNode, C> tree = NoIconTree.this;
            private TreeStore<SimpleNode> treeStore = NoIconTree.this.getStore();

            private void checkSubTree(SimpleNode item, CheckState state) {
                if (treeStore.hasChildren(item))
                    for (SimpleNode child : treeStore.getChildren(item)) {
                        tree.setChecked(child, state);
                        checkSubTree(child, state);
                    }
            }


            private void unCheckParent(SimpleNode item) {
                SimpleNode parent = treeStore.getParent(item);
                if (parent == null) return;
                boolean hasChecked = false;
                for (SimpleNode ch : treeStore.getChildren(parent)) {
                    if (tree.getChecked(ch).equals(CheckState.CHECKED) || tree.getChecked(ch).equals(CheckState.PARTIAL)) {
                        tree.setChecked(parent, CheckState.PARTIAL);
                        hasChecked = true;
                        break;
                    }
                }
                if (!hasChecked)
                    tree.setChecked(parent, CheckState.UNCHECKED);

                unCheckParent(parent);
            }


        });
    }

    public void checkParent(SimpleNode item) {
        SimpleNode parent = store.getParent(item);
        if (parent == null) return;

        boolean hasUnchecked = false;

        for (SimpleNode ch : store.getChildren(parent)) {
            if (!isChecked(ch) || CheckState.PARTIAL.equals(getChecked(ch))) {
                setChecked(parent, CheckState.PARTIAL);
                hasUnchecked = true;
                break;
            }
        }

        if (!hasUnchecked)
            setChecked(parent, CheckState.CHECKED);

        checkParent(parent);
    }



    public void setCheckedWithParent (SimpleNode item) {
        setChecked(item, Tree.CheckState.CHECKED);
        checkParent(item);
    }


    public void setCheckedExpandedWithParent (SimpleNode item) {
        setChecked(item, Tree.CheckState.CHECKED);
        checkParent(item);
        setExpanded(item, true, false);
    }

    @UiConstructor
    public NoIconTree(TreeStore<SimpleNode> store, ValueProvider<? super SimpleNode, C> valueProvider) {
        super(store, valueProvider);
    }

    @Override
    protected ImageResource calculateIconStyle(SimpleNode model) {
        return null;
    }

    /**
     * disable ability to check/unCheck, collapse/expand actions
     */
    @Override
    public void disable() {
        super.disable();
        setDisabled(true);
    }

    /**
     * disable ability to check/unCheck, collapse/expand actions
     */
    @Override
    public void enable() {
        super.enable();
        setDisabled(false);
    }

    public void enable(boolean enableTree) {
        if (enableTree) {
            enable();
        }
    }

    public void enableTree() {
        this.enable();
        this.enableEvents();
    }

    public void clearStore() {
        store.clear();
    }

    /**
     * results should be chosen from both Summary and Details subtree
     * @return List<TaskDataDto> to use the same link creator.
     */
    public Set<TaskDataDto> getSelectedTests() {

        Set<TaskDataDto> resultSet = new HashSet<TaskDataDto>();
        for (TestNode testNode : rootNode.getSummary().getTests()) {
            if (isChosen(testNode)) {
                resultSet.add(testNode.getTaskDataDto());
            }
        }
        for (TestDetailsNode testNode : rootNode.getDetailsNode().getTests()) {
            if (isChosen(testNode)) {
                resultSet.add(testNode.getTaskDataDto());
            }
        }

        return resultSet;
    }


    /**
     * @return MetricNameDto from all Tests
     */
    public Set<MetricNameDto> getCheckedMetrics() {

        Set<MetricNameDto> resultSet = new HashSet<MetricNameDto>();
        for (TestNode test : rootNode.getSummary().getTests()) {
            for (MetricNode metricNode : test.getMetrics()) {
                if (isChecked(metricNode)) {
                    resultSet.add(metricNode.getMetricName());
                }
            }
        }
        return resultSet;
    }


    /**
     * @return checked PlotNameDto from all Tests
     */
    public Set<PlotNameDto> getCheckedPlots() {

        Set<PlotNameDto> resultSet = new HashSet<PlotNameDto>();
        for (TestDetailsNode test : rootNode.getDetailsNode().getTests()) {
            for (PlotNode plotNode : test.getPlots()) {
                if (isChecked(plotNode)) {
                    resultSet.add(plotNode.getPlotName());
                }
            }
        }
        return resultSet;
    }


    /**
     * @return checked PlotNameDto for session scope plots
     */
    public Set<PlotNameDto> getCheckedSessionScopePlots() {
        if (rootNode.getDetailsNode().getSessionScopePlotsNode() == null) {
            return Collections.EMPTY_SET;
        }
        Set<PlotNameDto> resultSet = new HashSet<PlotNameDto>();
        for (SessionPlotNode plotNode : rootNode.getDetailsNode().getSessionScopePlotsNode().getPlots()) {
            if (isChecked(plotNode)) {
                resultSet.add(plotNode.getPlotNameDto());
            }
        }
        return resultSet;
    }


    /**
     * return false if CheckState = Tree.CheckState.UNCHECKED
     *        true in other cases
     * @param model tree model
     * @return bool
     */
    public boolean isChosen(SimpleNode model) {
        return !CheckState.UNCHECKED.equals(getChecked(model));
    }

    public void onSummaryTrendsTab() {
        onMetricsTab(false);
    }

    public void onMetricsTab() {
        onMetricsTab(true);
    }

    private void onMetricsTab(boolean boo) {
        if (rootNode != null) {
            setExpanded(rootNode.getSummary(), !boo);
            setExpanded(rootNode.getDetailsNode(), boo);
        }
    }
}