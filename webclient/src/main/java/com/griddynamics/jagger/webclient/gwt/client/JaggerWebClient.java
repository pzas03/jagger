package com.griddynamics.jagger.webclient.gwt.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.griddynamics.jagger.webclient.gwt.client.layout.JaggerWebClientLayout;
import com.griddynamics.jagger.webclient.gwt.client.mvp.JaggerWebClientActivityMapper;
import com.griddynamics.jagger.webclient.gwt.client.mvp.JaggerWebClientPlaceHistoryMapper;
import com.griddynamics.jagger.webclient.gwt.client.mvp.place.TrendsPlace;


/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/23/12
 */
public class JaggerWebClient implements EntryPoint {
    private SimpleLayoutPanel contentPanel;
    private TrendsPlace defaultPlace = new TrendsPlace();

    @Override
    public void onModuleLoad() {
        final JaggerWebClientLayout layout = new JaggerWebClientLayout();

        contentPanel = layout.getContentPanel();

        final ClientFactory clientFactory = GWT.create(ClientFactory.class);
        EventBus eventBus = clientFactory.getEventBus();
        PlaceController placeController = clientFactory.getPlaceController();

        // activate activity manager and init display
        ActivityMapper activityMapper = new JaggerWebClientActivityMapper(clientFactory);
        ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
        activityManager.setDisplay(contentPanel);

        // display default view with activated history processing
        JaggerWebClientPlaceHistoryMapper historyMapper = GWT.create(JaggerWebClientPlaceHistoryMapper.class);
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, defaultPlace);

        RootLayoutPanel.get().add(layout);

        History.newItem("trends:");
    }
}
