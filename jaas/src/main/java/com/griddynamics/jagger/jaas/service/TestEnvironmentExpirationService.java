package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.jaas.storage.TestEnvironmentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TestEnvironmentExpirationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestEnvironmentExpirationService.class);

    private TestEnvironmentDao testEnvironmentDao;

    @Autowired
    public TestEnvironmentExpirationService(TestEnvironmentDao testEnvironmentDao) {
        this.testEnvironmentDao = testEnvironmentDao;
    }

    @Scheduled(fixedRateString = "${environments.cleaning.job.periodicity}")
    public void deleteExpiredEnvironmentsTask() {
        long nowTimestampInUtc = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        int deleted = testEnvironmentDao.deleteExpired(nowTimestampInUtc);
        LOGGER.info(deleted + " expired test environments deleted.");
    }
}
