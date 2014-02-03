package com.griddynamics.jagger.webclient.server;

import com.griddynamics.jagger.webclient.client.ValidationService;
import com.griddynamics.jagger.webclient.client.mvp.NameTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ValidationServiceImpl implements ValidationService {

    Logger log = LoggerFactory.getLogger(ValidationServiceImpl.class);
    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean validateDatabaseModel() {

        try {
            entityManager.createQuery("select 1 from MetricDescriptionEntity").getSingleResult();
        } catch (Exception e) {
            log.error("Can't determine MetricDescriptionEntity column in your database.\n {}", NameTokens.EXCEPTION_MESSAGE_NO_METRICDESCRIPTION_TABLE);
            return false;
        }
        return true;

    }

}
