package com.griddynamics.jagger.webclient.gwt.client.mvp.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/25/12
 */
public class ViewResultsPlace extends Place {
    private static final String VIEW_HISTORY_TOKEN = "viewResults";

    public ViewResultsPlace() {
    }

    @Prefix(value = VIEW_HISTORY_TOKEN)
    public static class Tokenizer implements PlaceTokenizer<ViewResultsPlace> {
        @Override
        public ViewResultsPlace getPlace(String token) {
            return new ViewResultsPlace();
        }

        @Override
        public String getToken(ViewResultsPlace place) {
            return "";
        }
    }
}
