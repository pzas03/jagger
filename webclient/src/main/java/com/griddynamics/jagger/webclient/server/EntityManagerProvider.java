package com.griddynamics.jagger.webclient.server;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class EntityManagerProvider {
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jagger");

    protected EntityManagerProvider() {
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
}
