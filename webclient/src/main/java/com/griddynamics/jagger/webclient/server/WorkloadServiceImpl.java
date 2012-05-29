package com.griddynamics.jagger.webclient.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.griddynamics.jagger.engine.e1.aggregator.workload.model.WorkloadDetails;
import com.griddynamics.jagger.webclient.client.WorkloadService;
import com.griddynamics.jagger.webclient.client.dto.WorkloadDetailsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class WorkloadServiceImpl extends RemoteServiceServlet implements WorkloadService {
    private static final Logger log = LoggerFactory.getLogger(WorkloadServiceImpl.class);

    @Override
    public List<WorkloadDetailsDto> getWorkloadDetailsForSession(List<String> sessionIds) {
        EntityManager entityManager = EntityManagerProvider.getEntityManagerFactory().createEntityManager();

        List<WorkloadDetailsDto> workloadDetailsDtoList;
        try {
            List<WorkloadDetails> workloadDetailsList = (List<WorkloadDetails>) entityManager.createQuery(
                    "select distinct workloadData.scenario from WorkloadData as workloadData where workloadData.sessionId in (:sessionIds)").setParameter("sessionIds", sessionIds).getResultList();

            if (workloadDetailsList == null) {
                return Collections.emptyList();
            }

            workloadDetailsDtoList = new ArrayList<WorkloadDetailsDto>(workloadDetailsList.size());
            for (WorkloadDetails workloadDetails : workloadDetailsList) {
                workloadDetailsDtoList.add(new WorkloadDetailsDto(workloadDetails.getId(), workloadDetails.getName()));
            }

            log.info("WorkloadDetails for session id = {} are: {}", sessionIds, workloadDetailsDtoList);
        } finally {
            entityManager.close();
        }

        return workloadDetailsDtoList;
    }
}
