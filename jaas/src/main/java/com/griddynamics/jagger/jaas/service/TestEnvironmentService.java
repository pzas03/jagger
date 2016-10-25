package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;

import java.util.List;

public interface TestEnvironmentService {
    TestEnvironmentEntity read(String envId);

    List<TestEnvironmentEntity> readAll();

    void create(TestEnvironmentEntity testEnvironment);

    void update(TestEnvironmentEntity testEnvironment);

    void createOrUpdate(TestEnvironmentEntity testEnvironment);

    void delete(String envId);
}
