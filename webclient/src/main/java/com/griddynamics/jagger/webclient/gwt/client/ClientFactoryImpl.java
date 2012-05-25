package com.griddynamics.jagger.webclient.gwt.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.griddynamics.jagger.webclient.gwt.client.mvp.view.TrendsView;
import com.griddynamics.jagger.webclient.gwt.client.mvp.view.ViewResultsView;
import com.griddynamics.jagger.webclient.gwt.client.mvp.view.trends.DefaultTrendsView;
import com.griddynamics.jagger.webclient.gwt.client.mvp.view.viewresults.DefaultViewResultsView;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/25/12
 */
public class ClientFactoryImpl implements ClientFactory {

    private final EventBus eventBus = new SimpleEventBus();
    private final PlaceController placeController = new PlaceController(eventBus);

    private final TrendsView trendsView = new DefaultTrendsView();
    private final ViewResultsView viewResultsView = new DefaultViewResultsView();

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public PlaceController getPlaceController() {
        return placeController;
    }

    @Override
    public TrendsView getTrendsView() {
        return trendsView;
    }

    @Override
    public ViewResultsView getViewResultsView() {
        return viewResultsView;
    }
}
