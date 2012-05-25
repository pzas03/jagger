package com.griddynamics.jagger.webclient.gwt.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.griddynamics.jagger.webclient.gwt.client.ClientFactory;
import com.griddynamics.jagger.webclient.gwt.client.mvp.activity.TrendsActivity;
import com.griddynamics.jagger.webclient.gwt.client.mvp.activity.ViewResultsActivity;
import com.griddynamics.jagger.webclient.gwt.client.mvp.place.TrendsPlace;
import com.griddynamics.jagger.webclient.gwt.client.mvp.place.ViewResultsPlace;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/25/12
 */
public class JaggerWebClientActivityMapper implements ActivityMapper {
    private ClientFactory clientFactory;

    public JaggerWebClientActivityMapper(ClientFactory clientFactory) {
        super();
        this.clientFactory = clientFactory;
    }

    @Override
    public Activity getActivity(Place place) {
        if (place instanceof TrendsPlace) {
            return new TrendsActivity(clientFactory);
        } else if (place instanceof ViewResultsPlace) {
            return new ViewResultsActivity(clientFactory);
        }
        return null;
    }
}
