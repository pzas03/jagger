package com.griddynamics.jagger.webclient.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public class MainView extends ResizeComposite implements IsWidget {

    interface MainViewUiBinder extends UiBinder<Widget, MainView> {
    }

    private static MainViewUiBinder uiBinder = GWT.create(MainViewUiBinder.class);

    @UiField
    DeckLayoutPanel contentContainer;

    public MainView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public AcceptsOneWidget getContentContainer() {
        return contentContainer;
    }
}
