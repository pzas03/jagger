package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.webclient.client.DownloadService;

/**
 * Class to contain all methods for file downloading.
 * Used to avoid thinking of AsyncCallback
 */
public class FileDownLoader {

    private final static String DOWNLOAD_SERVLET_PATH = "download";

    /**
     * download plot in csv for MetricNode */
    public static void downloadPlotInCsv(final MetricNode metricNode) {

        DownloadService.Async.getInstance().createPlotCsvFile(metricNode, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                new ExceptionPanel("Failed to create cvs file for " + metricNode.getDisplayName() + " :\n" + caught.getMessage());
            }

            @Override
            public void onSuccess(String result) {

                String url = GWT.getHostPageBaseURL() + DOWNLOAD_SERVLET_PATH + "?fileKey=" + result;
                // start downloading
                Window.Location.assign(url);
            }
        });
    }
}
