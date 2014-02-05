package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.CommonDataService;
import com.griddynamics.jagger.webclient.client.data.WebClientProperties;
import org.springframework.beans.factory.annotation.Required;

public class CommonDataServiceImpl implements CommonDataService {

    private boolean userCommentAvailable;
    private int userCommentMaxLength;

    @Required
    public void setUserCommentAvailable(boolean userCommentAvailable) {
        this.userCommentAvailable = userCommentAvailable;
    }

    @Required
    public void setUserCommentMaxLength(int userCommentMaxLength) {
        this.userCommentMaxLength = userCommentMaxLength;
    }

    @Override
    public WebClientProperties getWebClientProperties() {
        WebClientProperties webClientProperties = new WebClientProperties();

        webClientProperties.setUserCommentAvailable(userCommentAvailable);
        webClientProperties.setUserCommentMaxLength(userCommentMaxLength);
        return webClientProperties;
    }
}
