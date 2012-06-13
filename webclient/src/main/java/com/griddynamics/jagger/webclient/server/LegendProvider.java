package com.griddynamics.jagger.webclient.server;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class LegendProvider {
    private EntityManager entityManager;

    public LegendProvider() {
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public String getPlotHeader(long taskId, String plotName) {
        Object[] legendData;
        legendData = (Object[]) entityManager.createQuery("select td.sessionId, td.taskName from TaskData as td where td.id=:taskId").
                setParameter("taskId", taskId).getSingleResult();

        return generatePlotHeader(legendData[0].toString(), legendData[1].toString(), plotName);
    }

    private String generatePlotHeader(String sessionId, String taskName, String plotName) {
        StringBuilder builder = new StringBuilder();
        builder
                .append("Session #")
                .append(sessionId)
                .append(", ")
                .append(taskName)
                .append(", ")
                .append(plotName);

        return builder.toString();
    }
}
