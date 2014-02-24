package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.CommonDataService;
import com.griddynamics.jagger.webclient.client.data.WebClientProperties;
import org.springframework.beans.factory.annotation.Required;

public class CommonDataServiceImpl implements CommonDataService {

    WebClientProperties webClientProperties;

    public void setWebClientProperties(WebClientProperties webClientProperties) {
        this.webClientProperties = webClientProperties;
    }

    @Override
    public WebClientProperties getWebClientProperties() {
        return webClientProperties;
    }
}
