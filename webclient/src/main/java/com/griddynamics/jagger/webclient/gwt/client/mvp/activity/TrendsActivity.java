package com.griddynamics.jagger.webclient.gwt.client.mvp.activity;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.griddynamics.jagger.webclient.gwt.client.ClientFactory;
import com.griddynamics.jagger.webclient.gwt.client.JaggerWebClientConstants;
import com.griddynamics.jagger.webclient.gwt.client.mvp.view.TrendsView;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/25/12
 */
public class TrendsActivity extends JaggerGenericActivity implements TrendsView.TrendsPresenter {
    private ClientFactory clientFactory;

    public TrendsActivity(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        applyCurrentLinkStyle(JaggerWebClientConstants.TRENDS_LINK_ID);

        final TrendsView view = clientFactory.getTrendsView();
        view.setPresenter(this);
        panel.setWidget(view.asWidget());
    }
}
