package com.griddynamics.jagger.webclient.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/28/12
 */
public class JaggerWebClient implements EntryPoint {
    private final Trends trends = new Trends();

    public void onModuleLoad() {
        JaggerResources.INSTANCE.css().ensureInjected();
        RootLayoutPanel.get().add(trends);
    }
}
