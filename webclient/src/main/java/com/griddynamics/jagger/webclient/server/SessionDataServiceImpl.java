package com.griddynamics.jagger.webclient.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.griddynamics.jagger.webclient.client.SessionDataService;
import com.griddynamics.jagger.webclient.client.dto.PagedSessionDataDto;
import com.griddynamics.jagger.webclient.client.dto.SessionDataDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 5/29/12
 */
public class SessionDataServiceImpl extends RemoteServiceServlet implements SessionDataService {
    private static final List<SessionDataDto> data = new ArrayList<SessionDataDto>();

    static {
        data.add(new SessionDataDto("Session 1", new Date(), new Date(), 1, 2, 0));
        data.add(new SessionDataDto("Session 2", new Date(), new Date(), 1, 2, 0));
        data.add(new SessionDataDto("Session 3", new Date(), new Date(), 1, 2, 0));
        data.add(new SessionDataDto("Session 4", new Date(), new Date(), 1, 2, 0));
        data.add(new SessionDataDto("Session 5", new Date(), new Date(), 1, 2, 0));
        data.add(new SessionDataDto("Session 6", new Date(), new Date(), 1, 2, 0));
        data.add(new SessionDataDto("Session 7", new Date(), new Date(), 1, 2, 0));
        data.add(new SessionDataDto("Session 8", new Date(), new Date(), 1, 2, 0));
        data.add(new SessionDataDto("Session 8", new Date(), new Date(), 1, 2, 0));
        data.add(new SessionDataDto("Session 8", new Date(), new Date(), 1, 2, 0));
        data.add(new SessionDataDto("Session 8", new Date(), new Date(), 1, 2, 0));
        data.add(new SessionDataDto("Session 8", new Date(), new Date(), 1, 2, 0));
        data.add(new SessionDataDto("Session 8", new Date(), new Date(), 1, 2, 0));
        data.add(new SessionDataDto("Session 8", new Date(), new Date(), 1, 2, 0));
    }

    @Override
    public PagedSessionDataDto getAll(int start, int length) {
        int end = start + length;
        end = end > data.size() ? data.size() : end;
        List<SessionDataDto> subList = data.subList(start, end);

        return new PagedSessionDataDto(new ArrayList<SessionDataDto>(subList), data.size());
    }
}
