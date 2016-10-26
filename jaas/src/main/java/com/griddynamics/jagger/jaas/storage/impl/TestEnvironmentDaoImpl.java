package com.griddynamics.jagger.jaas.storage.impl;

import com.griddynamics.jagger.jaas.storage.AbstractCrudDao;
import com.griddynamics.jagger.jaas.storage.TestEnvironmentDao;
import com.griddynamics.jagger.jaas.storage.model.TestEnvironmentEntity;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.hibernate.criterion.Projections.rowCount;

/**
 * Hibernate based transactional implementation of {@link com.griddynamics.jagger.jaas.storage.CrudDao} interface for
 * {@link TestEnvironmentEntity}.
 */
@SuppressWarnings("unchecked")
@Repository
public class TestEnvironmentDaoImpl extends AbstractCrudDao<TestEnvironmentEntity, String> implements TestEnvironmentDao {

    @Override
    @Transactional
    public TestEnvironmentEntity read(String testEnvironmentId) {
        return (TestEnvironmentEntity) getCurrentSession().get(TestEnvironmentEntity.class, testEnvironmentId);
    }

    @Override
    @Transactional
    public List<TestEnvironmentEntity> readAll() {
        return getCurrentSession().createCriteria(TestEnvironmentEntity.class).list();
    }

    @Override
    @Transactional
    public void create(TestEnvironmentEntity testEnvironment) {
        getCurrentSession().save(testEnvironment);
    }

    @Override
    @Transactional
    public void create(Iterable<TestEnvironmentEntity> testEnvironments) {
        Objects.requireNonNull(testEnvironments);
        testEnvironments.forEach(env -> getCurrentSession().save(env));
    }

    @Override
    @Transactional
    public void update(TestEnvironmentEntity testEnvironment) {
        getCurrentSession().update(testEnvironment);
    }

    @Override
    @Transactional
    public void createOrUpdate(TestEnvironmentEntity testEnvironment) {
        getCurrentSession().saveOrUpdate(testEnvironment);
    }

    @Override
    @Transactional
    public void delete(String testEnvironmentId) {
        TestEnvironmentEntity testEnvironmentEntity =
                (TestEnvironmentEntity) getCurrentSession().get(TestEnvironmentEntity.class, testEnvironmentId);
        delete(testEnvironmentEntity);
    }

    @Override
    @Transactional
    public void delete(TestEnvironmentEntity testEnvironment) {
        getCurrentSession().delete(testEnvironment);
    }

    @Override
    @Transactional
    public long count() {
        return (Long) getCurrentSession().createCriteria(TestEnvironmentEntity.class).setProjection(rowCount()).uniqueResult();
    }

    @Override
    @Transactional
    public boolean exists(String testEnvironmentId) {
        Query query = getCurrentSession().createQuery("select 1 from TestEnvironmentEntity t where t.environmentId = :id");
        query.setString("id", testEnvironmentId);
        return query.uniqueResult() != null;
    }
}
