package com.griddynamics.jagger.jaas.service.impl;

import com.griddynamics.jagger.jaas.exceptions.TestSuiteNotFoundException;
import com.griddynamics.jagger.jaas.exceptions.WrongTestEnvironmentStatusException;
import com.griddynamics.jagger.jaas.service.TestEnvironmentService;
import com.griddynamics.jagger.jaas.storage.TestEnvironmentDao;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import com.griddynamics.jagger.jaas.storage.model.TestSuiteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity.TestEnvironmentStatus.PENDING;
import static com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity.TestEnvironmentStatus.RUNNING;
import static java.lang.String.format;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections.CollectionUtils.isEqualCollection;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class TestEnvironmentServiceImpl implements TestEnvironmentService {

    private TestEnvironmentDao testEnvironmentDao;

    @Autowired
    public TestEnvironmentServiceImpl(TestEnvironmentDao testEnvironmentDao) {
        this.testEnvironmentDao = testEnvironmentDao;
    }

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
        fillTestSuites(testEnvironment);
        testEnvironmentDao.create(testEnvironment);
    }

    @Override
    public void update(TestEnvironmentEntity newTestEnv) {
        TestEnvironmentEntity testEnvToUpdate = read(newTestEnv.getEnvironmentId());

        if (newTestEnv.getRunningTestSuite() != null && newTestEnv.getStatus() == RUNNING
                || newTestEnv.getRunningTestSuite() == null && newTestEnv.getStatus() == PENDING)
            testEnvToUpdate.setStatus(newTestEnv.getStatus());
        else
            throw new WrongTestEnvironmentStatusException(newTestEnv.getStatus(), newTestEnv.getRunningTestSuite());

        if (testEnvToUpdate.getTestSuites() == null)
            testEnvToUpdate.setTestSuites(newTestEnv.getTestSuites());
        else if (newTestEnv.getTestSuites() == null)
            testEnvToUpdate.getTestSuites().clear();
        else if (!isEqualCollection(testEnvToUpdate.getTestSuites(), newTestEnv.getTestSuites())) {
            HashSet<TestSuiteEntity> newTestSuites = newHashSet(testEnvToUpdate.getTestSuites());
            // add all new test suites
            newTestSuites.addAll(newTestEnv.getTestSuites());

            // remove all test suites which must be deleted
            newHashSet(newTestSuites).stream().filter(s -> !newTestEnv.getTestSuites().contains(s)).forEach(newTestSuites::remove);

            testEnvToUpdate.getTestSuites().clear();
            testEnvToUpdate.getTestSuites().addAll(newTestSuites);
        }

        if (testEnvToUpdate.getRunningTestSuite() != newTestEnv.getRunningTestSuite()) {
            testEnvToUpdate.setRunningTestSuite(getNewRunningTestSuite(newTestEnv, testEnvToUpdate));
        }
        fillTestSuites(testEnvToUpdate);
        testEnvironmentDao.update(testEnvToUpdate);
    }

    @Override
    public void delete(String envId) {
        testEnvironmentDao.delete(envId);
    }

    @Override
    public boolean exists(String envId) {
        return testEnvironmentDao.exists(envId);
    }

    private TestSuiteEntity getNewRunningTestSuite(TestEnvironmentEntity newTestEnv, TestEnvironmentEntity testEnvToUpdate) {
        if (newTestEnv.getRunningTestSuite() == null)
            return null;

        if (isNotEmpty(testEnvToUpdate.getTestSuites())) {
            Map<String, TestSuiteEntity> testSuites = testEnvToUpdate.getTestSuites().stream()
                    .collect(toMap(TestSuiteEntity::getTestSuiteId, identity()));
            TestSuiteEntity newRunningTestSuite = testSuites.get(newTestEnv.getRunningTestSuite().getTestSuiteId());

            return Optional.ofNullable(newRunningTestSuite).orElseThrow(() -> new TestSuiteNotFoundException(
                    format("Running TestSuite[id=%s] cannot be set to TestEnvironment[id=%s]. Possible TestSuites: %s.",
                            newTestEnv.getRunningTestSuite().getTestSuiteId(), newTestEnv.getEnvironmentId(), testSuites.keySet())));
        }

        throw new TestSuiteNotFoundException(
                format("Running TestSuite[id=%s] cannot be set to TestEnvironment[id=%s], since it doesn't belong to it.",
                        newTestEnv.getRunningTestSuite().getTestSuiteId(), newTestEnv.getEnvironmentId()));
    }

    private void fillTestSuites(TestEnvironmentEntity testEnv) {
        if (isNotEmpty(testEnv.getTestSuites()))
            testEnv.getTestSuites().stream()
                    .filter(suite -> suite.getTestEnvironmentEntity() == null)
                    .forEach(suite -> suite.setTestEnvironmentEntity(testEnv));
        if (testEnv.getRunningTestSuite() != null && testEnv.getRunningTestSuite().getTestEnvironmentEntity() == null)
            testEnv.getRunningTestSuite().setTestEnvironmentEntity(testEnv);
    }
}
