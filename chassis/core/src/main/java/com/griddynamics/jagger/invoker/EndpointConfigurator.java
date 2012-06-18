package com.griddynamics.jagger.invoker;

import java.util.LinkedList;
import java.util.List;

public class EndpointConfigurator {
    private String urls;

    public void setUrls(String urls) {
        this.urls = urls;
    }

    public List<String> getEndpoints(String path) {
        String[] urls = this.urls.split(",");

        List<String> result = new LinkedList<String>();
        for (String url : urls) {
            result.add(url.trim() + path);
        }

        return result;
    }

    public List<String> getEndpoints() {
        String[] urls = this.urls.split(",");

        List<String> result = new LinkedList<String>();
        for (String url : urls) {
            result.add(url.trim());
        }

        return result;
    }

    public String getEndpoint(String path) {
        return getEndpoints(path).get(0);
    }
}
