package com.griddynamics.jagger.webclient.gwt.client.mvp.view.viewresults;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.griddynamics.jagger.webclient.gwt.client.mvp.view.ViewResultsView;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/25/12
 */
public class DefaultViewResultsView extends Composite implements ViewResultsView {

    interface DefaultViewResultsViewUiBinder extends UiBinder<Widget, ViewResultsView> { }
    private static DefaultViewResultsViewUiBinder uiBinder = GWT.create(DefaultViewResultsViewUiBinder.class);

    private ViewResultsPresenter presenter;

    public DefaultViewResultsView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(ViewResultsPresenter presenter) {
        this.presenter = presenter;
    }
}
