package com.griddynamics.jagger.jaas.service.impl;

import com.griddynamics.jagger.jaas.service.TestEnvironmentService;
import com.griddynamics.jagger.jaas.storage.TestEnvironmentDao;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Service
public class TestEnvironmentServiceImpl implements TestEnvironmentService {

    @Autowired
    private TestEnvironmentDao testEnvironmentDao;

    @Override
    public TestEnvironmentEntity read(String envId) {
        return testEnvironmentDao.read(envId);
    }

    @Override
    public List<TestEnvironmentEntity> readAll() {
        return newArrayList(testEnvironmentDao.readAll());
    }

    @Override
    public void create(TestEnvironmentEntity testEnvironment) {
        testEnvironmentDao.create(testEnvironment);
    }

    @Override
    public void update(TestEnvironmentEntity testEnvironment) {
        testEnvironmentDao.update(testEnvironment);
    }

    @Override
    public void createOrUpdate(TestEnvironmentEntity testEnvironment) {
        testEnvironmentDao.createOrUpdate(testEnvironment);
    }

    @Override
    public void delete(String envId) {
        testEnvironmentDao.delete(envId);
    }
}
