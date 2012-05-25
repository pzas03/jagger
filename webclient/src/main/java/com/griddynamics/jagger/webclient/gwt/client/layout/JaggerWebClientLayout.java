package com.griddynamics.jagger.webclient.gwt.client.layout;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/25/12
 */
public class JaggerWebClientLayout extends Composite {
    interface JaggerWebClientLayoutUiBinder extends UiBinder<Widget, JaggerWebClientLayout> {
    }

    private static JaggerWebClientLayoutUiBinder ourUiBinder = GWT.create(JaggerWebClientLayoutUiBinder.class);

    @UiField
    SimpleLayoutPanel contentPanel;

    public JaggerWebClientLayout() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public SimpleLayoutPanel getContentPanel() {
        return contentPanel;
    }
}