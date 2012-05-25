package com.griddynamics.jagger.webclient.gwt.client.mvp;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import com.griddynamics.jagger.webclient.gwt.client.mvp.place.TrendsPlace;
import com.griddynamics.jagger.webclient.gwt.client.mvp.place.ViewResultsPlace;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/25/12
 */
@WithTokenizers({TrendsPlace.Tokenizer.class, ViewResultsPlace.Tokenizer.class})
public interface JaggerWebClientPlaceHistoryMapper extends PlaceHistoryMapper {
}
