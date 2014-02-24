package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.CommonDataService;
import com.griddynamics.jagger.webclient.client.data.WebClientProperties;
import org.springframework.beans.factory.annotation.Required;

public class CommonDataServiceImpl implements CommonDataService {

    private CommonDataProvider commonDataProvider;
    private boolean userCommentEditAvailable;
    private boolean userCommentStoreAvailable;
    private int userCommentMaxLength;

    @Required
    public void setUserCommentEditAvailable(boolean userCommentEditAvailable) {
        this.userCommentEditAvailable = userCommentEditAvailable;
    }

    @Required
    public void setUserCommentMaxLength(int userCommentMaxLength) {
        this.userCommentMaxLength = userCommentMaxLength;
    }

    @Required
    public void setCommonDataProvider(CommonDataProvider commonDataProvider) {
        this.commonDataProvider = commonDataProvider;
        checkIfUserCommentAvailable();
    }

    public boolean isUserCommentEditAvailable() {
        return userCommentEditAvailable;
    }

    public int getUserCommentMaxLength() {
        return userCommentMaxLength;
    }

    public boolean isUserCommentStoreAvailable() {
        return userCommentStoreAvailable;
    }

    @Override
    public WebClientProperties getWebClientProperties() {
        WebClientProperties webClientProperties = new WebClientProperties();

        webClientProperties.setUserCommentEditAvailable(userCommentEditAvailable);
        webClientProperties.setUserCommentMaxLength(userCommentMaxLength);
        webClientProperties.setUserCommentStoreAvailable(userCommentStoreAvailable);
        return webClientProperties;
    }

    private void checkIfUserCommentAvailable() {
        userCommentStoreAvailable = commonDataProvider.checkIfUserCommentStorageAvailable();
    }
}
