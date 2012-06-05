package com.griddynamics.jagger.webclient.server;

import javax.persistence.EntityManager;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class LegendProvider {

    public LegendProvider() {
    }

    public String getPlotHeader(long taskId, String plotName) {
        EntityManager entityManager = EntityManagerProvider.getEntityManagerFactory().createEntityManager();
        Object[] legendData;
        try {
            legendData = (Object[]) entityManager.createQuery("select td.sessionId, td.taskName from TaskData as td where td.id=:taskId").
                    setParameter("taskId", taskId).getSingleResult();
        } finally {
            entityManager.close();
        }

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
