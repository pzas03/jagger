package com.griddynamics.jagger.webclient.gwt.client.mvp.view;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/25/12
 */
public interface TrendsView extends IsWidget {
    void setPresenter(TrendsPresenter presenter);

    public interface TrendsPresenter {}
}
