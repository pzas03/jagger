package com.griddynamics.jagger.webclient.gwt.client.mvp.activity;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.griddynamics.jagger.webclient.gwt.client.ClientFactory;
import com.griddynamics.jagger.webclient.gwt.client.JaggerWebClientConstants;
import com.griddynamics.jagger.webclient.gwt.client.mvp.view.ViewResultsView;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/25/12
 */
public class ViewResultsActivity extends JaggerGenericActivity implements ViewResultsView.ViewResultsPresenter {
    private ClientFactory clientFactory;

    public ViewResultsActivity(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        applyCurrentLinkStyle(JaggerWebClientConstants.VIEW_RESULTS_LINK_ID);

        final ViewResultsView view = clientFactory.getViewResultsView();
        view.setPresenter(this);
        panel.setWidget(view.asWidget());
    }
}
