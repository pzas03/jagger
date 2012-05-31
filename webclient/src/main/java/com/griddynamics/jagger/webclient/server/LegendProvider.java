package com.griddynamics.jagger.webclient.server;

import javax.persistence.EntityManager;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class LegendProvider {

    public LegendProvider() {
    }

    public String getPlotLegend(long taskId, Plot plot, String unitOfMeasurement) {
        EntityManager entityManager = EntityManagerProvider.getEntityManagerFactory().createEntityManager();
        Object[] legendData;
        try {
            legendData = (Object[]) entityManager.createQuery("select td.sessionId, td.taskName from TaskData as td where td.id=:taskId").
                    setParameter("taskId", taskId).getSingleResult();
        } finally {
            entityManager.close();
        }

        return generatePlotLegend(legendData[0].toString(), legendData[1].toString(), plot, unitOfMeasurement);
    }

    private String generatePlotLegend(String sessionId, String taskName, Plot plot, String unitOfMeasurement) {
        StringBuilder builder = new StringBuilder();
        builder
                .append("Session ")
                .append(sessionId)
                .append(", ")
                .append(taskName)
                .append(", ")
                .append(plot.getText())
                .append(" (")
                .append(unitOfMeasurement)
                .append(")");

        return builder.toString();
    }
}
