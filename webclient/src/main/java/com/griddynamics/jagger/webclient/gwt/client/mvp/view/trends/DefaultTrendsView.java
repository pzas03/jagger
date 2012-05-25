package com.griddynamics.jagger.webclient.gwt.client.mvp.view.trends;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.griddynamics.jagger.webclient.gwt.client.mvp.view.TrendsView;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/25/12
 */
public class DefaultTrendsView extends Composite implements TrendsView {

    interface DefaultTrendsViewUiBinder extends UiBinder<Widget, TrendsView> { }
    private static DefaultTrendsViewUiBinder uiBinder = GWT.create(DefaultTrendsViewUiBinder.class);

    private TrendsPresenter presenter;

    public DefaultTrendsView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(TrendsPresenter presenter) {
        this.presenter = presenter;
    }
}
