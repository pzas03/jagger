package com.griddynamics.jagger.webclient.gwt.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.griddynamics.jagger.webclient.gwt.client.mvp.view.TrendsView;
import com.griddynamics.jagger.webclient.gwt.client.mvp.view.ViewResultsView;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/25/12
 */
public interface ClientFactory {

    public EventBus getEventBus();
    public PlaceController getPlaceController();

    TrendsView getTrendsView();
    ViewResultsView getViewResultsView();
}
