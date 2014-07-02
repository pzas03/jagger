package com.griddynamics.jagger.webclient.client.utils;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.griddynamics.jagger.util.TimeUtils;

/**
 * Used to get same date formatter in every place in client code
 */
public class DateFormatterHolder {

    private static DateTimeFormat dateFormatter;

    public static DateTimeFormat getDateFormatter() {
        if (dateFormatter == null) {
            dateFormatter = DateTimeFormat.getFormat(TimeUtils.DATE_FORMAT);
        }
        return dateFormatter;
    }
}
