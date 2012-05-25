package com.griddynamics.jagger.webclient.gwt.client.mvp.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/25/12
 */
public class TrendsPlace extends Place {
    private static final String VIEW_HISTORY_TOKEN = "trends";

    public TrendsPlace() {
    }

    @Prefix(value = VIEW_HISTORY_TOKEN)
    public static class Tokenizer implements PlaceTokenizer<TrendsPlace> {
        @Override
        public TrendsPlace getPlace(String token) {
            return new TrendsPlace();
        }

        @Override
        public String getToken(TrendsPlace place) {
            return "";
        }
    }
}
