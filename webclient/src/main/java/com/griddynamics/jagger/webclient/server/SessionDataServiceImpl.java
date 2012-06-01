package com.griddynamics.jagger.webclient.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.griddynamics.jagger.engine.e1.aggregator.session.model.SessionData;
import com.griddynamics.jagger.webclient.client.SessionDataService;
import com.griddynamics.jagger.webclient.client.dto.PagedSessionDataDto;
import com.griddynamics.jagger.webclient.client.dto.SessionDataDto;
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
public class SessionDataServiceImpl extends RemoteServiceServlet implements SessionDataService {
    private static final Logger log = LoggerFactory.getLogger(SessionDataServiceImpl.class);

    @Override
    public PagedSessionDataDto getAll(int start, int length) {
        long timestamp = System.currentTimeMillis();
        EntityManager entityManager = EntityManagerProvider.getEntityManagerFactory().createEntityManager();
        long totalSize;
        List<SessionDataDto> sessionDataDtoList;
        try {
            totalSize = (Long) entityManager.createQuery("select count(sessionData.id) from SessionData as sessionData").getSingleResult();
            List<SessionData> sessionDataList = (List<SessionData>)
                    entityManager.createQuery("select sd from SessionData as sd order by sd.startTime asc").setFirstResult(start).setMaxResults(length).getResultList();

            if (sessionDataList == null) {
                return new PagedSessionDataDto(Collections.<SessionDataDto>emptyList(), 0);
            }

            sessionDataDtoList = new ArrayList<SessionDataDto>(sessionDataList.size());
            for (SessionData sessionData : sessionDataList) {
                sessionDataDtoList.add(new SessionDataDto(
                        sessionData.getSessionId(),
                        sessionData.getStartTime(),
                        sessionData.getEndTime(),
                        sessionData.getActiveKernels(),
                        sessionData.getTaskExecuted(),
                        sessionData.getTaskFailed())
                );
            }

            log.info("There was loaded {} sessions data from {} for {} ms", new Object[] {sessionDataDtoList.size(), totalSize, System.currentTimeMillis()-timestamp});
        } finally {
            entityManager.close();
        }

        return new PagedSessionDataDto(sessionDataDtoList, (int) totalSize);
    }
}
