package com.griddynamics.jagger.webclient.gwt.client.mvp.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.griddynamics.jagger.webclient.gwt.client.JaggerWebClientConstants;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/25/12
 */
public abstract class JaggerGenericActivity extends AbstractActivity {
    private static Map<String, Element> navLinks = new LinkedHashMap<String, Element>();

    static {
        navLinks.put(JaggerWebClientConstants.TRENDS_LINK_ID, DOM.getElementById(JaggerWebClientConstants.TRENDS_LINK_ID));
        navLinks.put(JaggerWebClientConstants.VIEW_RESULTS_LINK_ID, DOM.getElementById(JaggerWebClientConstants.VIEW_RESULTS_LINK_ID));
    }

    public void applyCurrentLinkStyle(String viewId) {
        for (String linkId : navLinks.keySet()) {
            final Element link = navLinks.get(linkId);
            if (link == null) continue;
            if (linkId.equals(viewId)) {
                link.addClassName(JaggerWebClientConstants.CURRENT_TAB_CLASS);
            } else {
                link.removeClassName(JaggerWebClientConstants.CURRENT_TAB_CLASS);
            }
        }
    }
}
