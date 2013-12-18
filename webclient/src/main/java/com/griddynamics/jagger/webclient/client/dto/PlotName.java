package com.griddynamics.jagger.webclient.client.dto;

import java.io.Serializable;

/**
 * User: amikryukov
 * Date: 12/17/13
 */
public class PlotName implements Serializable {

    protected String plotName;
    protected String displayName;

    public String getPlotName() {
        return plotName;
    }

    public void setPlotName(String plotName) {
        this.plotName = plotName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplay() {
        return displayName == null ? plotName : displayName;
    }
}
