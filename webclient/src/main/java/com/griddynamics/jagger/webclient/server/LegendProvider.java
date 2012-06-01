package com.griddynamics.jagger.webclient.server;

import javax.persistence.EntityManager;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/31/12
 */
public class LegendProvider {

    public LegendProvider() {
    }

    public String getPlotLegend(Plot plot) {
        return generatePlotLegend(plot.getText(), plot.getUnitOfMeasurement());
    }

    public String getPlotLegend(String plot, String unitOfMeasurement) {
        return generatePlotLegend(plot, unitOfMeasurement);
    }

    public String getPlotHeader(long taskId, Plot plot) {
        EntityManager entityManager = EntityManagerProvider.getEntityManagerFactory().createEntityManager();
        Object[] legendData;
        try {
            legendData = (Object[]) entityManager.createQuery("select td.sessionId, td.taskName from TaskData as td where td.id=:taskId").
                    setParameter("taskId", taskId).getSingleResult();
        } finally {
            entityManager.close();
        }

        return generatePlotHeader(legendData[0].toString(), legendData[1].toString(), plot);
    }

    private String generatePlotLegend(String plotName, String unitOfMeasurement) {
        StringBuilder builder = new StringBuilder();
        builder
                .append(plotName)
                .append(" (")
                .append(unitOfMeasurement)
                .append(")");

        return builder.toString();
    }

    private String generatePlotHeader(String sessionId, String taskName, Plot plot) {
        StringBuilder builder = new StringBuilder();
        builder
                .append("Session #")
                .append(sessionId)
                .append(", ")
                .append(taskName)
                .append(", ")
                .append(plot.getText());

        return builder.toString();
    }
}
