package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.griddynamics.jagger.webclient.client.dto.TaskDataDto;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kirilkadurilka
 * Date: 26.03.13
 * Time: 12:30
 * To change this template use File | Settings | File Templates.
 */
public class SessionComparisonPanel extends VerticalPanel implements SessionPanel{


    @Override
    public void update(Set<TaskDataDto> tests) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addTest(TaskDataDto test) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeTest(TaskDataDto test) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void showMetric(TaskDataDto test, String metricName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void hideMetric(TaskDataDto test, String metricName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void showMetric(String metricName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void hideMetric(String metricName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
