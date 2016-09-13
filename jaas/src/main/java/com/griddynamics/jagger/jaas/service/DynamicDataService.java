package com.griddynamics.jagger.jaas.service;

import com.griddynamics.jagger.config.DataServiceConfig;
import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.jaas.storage.DbConfigEntityDao;
import com.griddynamics.jagger.jaas.storage.model.DbConfigEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Provides {@link com.griddynamics.jagger.engine.e1.services.DataService} service
 * based on configuration described by {@link DbConfigEntity}
 * and handles storage for {@link DbConfigEntityDao} entities.
 */
@Service
public class DynamicDataService implements DbConfigEntityDao {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataService.class);
    
    private final ExecutorService destroyerService =
            Executors.newSingleThreadExecutor(r -> new Thread("DynamicDataServiceDestoyer"));
    
    private final ConcurrentMap<String, AbstractApplicationContext> dataServiceContexts = new ConcurrentHashMap<>();
    
    @Autowired
    private DbConfigEntityDao jaasDao;
    
    @Value("${jaas.data.service.cache.size}")
    private int dataServiceCacheSize = 10;
    
    @Autowired
    private DbConfigEntity defaultDbConfigEntity;
    
    @PostConstruct
    public void init() {
        LOGGER.info("Registering default jagger test db config: {}", defaultDbConfigEntity);
        jaasDao.createOrUpdate(defaultDbConfigEntity);
        getDataServiceFor(defaultDbConfigEntity.getName());
    }
    
    @PreDestroy
    public void destroy() {
        destroyerService.shutdown();
    }
    
    protected void evictDataServiceFor(final String configName) {
        LOGGER.info("Evicting jagger test db config with name: {}", configName);
        AbstractApplicationContext context = dataServiceContexts.remove(configName);
        if (context != null) {
            destroyerService.execute(context::destroy);
        }
    }
    
    public DataService getDataServiceFor(final String configName) {
        
        Objects.requireNonNull(configName);
        
        ApplicationContext dataServiceContext = dataServiceContexts.get(configName);
        if (Objects.isNull(dataServiceContext)) {
            DbConfigEntity config = jaasDao.read(configName);
            if (Objects.isNull(config)) {
                return null;
            }
            synchronized (this) {
                dataServiceContext = dataServiceContexts.computeIfAbsent(configName, s -> {
                    if (dataServiceContexts.size() >= dataServiceCacheSize) {
                        evictDataServiceFor(dataServiceContexts.keySet().iterator().next());
                    }
                    return initDataServiceContextFor(config);
                });
            }
        }
        
        return dataServiceContext.getBean(DataService.class);
    }
    
    protected AbstractApplicationContext initDataServiceContextFor(DbConfigEntity config) {
    
        LOGGER.debug("Initializing spring context for jagger test db config: {}", config);

        PropertiesPropertySource propertySource =
                new PropertiesPropertySource(this.getClass().getName(), extractPropsFrom(config));
        
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.getEnvironment().getPropertySources().addFirst(propertySource);
        applicationContext.register(DataServiceConfig.class);
        applicationContext.refresh();

        LOGGER.debug("Spring context has been initialized for jagger test db config: {}", config);
        return applicationContext;
    }
    
    public Properties extractPropsFrom(DbConfigEntity config) {
        Properties configProps = new Properties();
        for (Field field : config.getClass().getDeclaredFields()) {
            JaggerPropertyName propertyName = field.getAnnotation(JaggerPropertyName.class);
            if (Objects.nonNull(propertyName)) {
                field.setAccessible(true);
                configProps.setProperty(propertyName.value(), (String) ReflectionUtils.getField(field, config));
            }
        }
        
        return configProps;
    }
    
    @Override
    public DbConfigEntity read(String configName) {
        return jaasDao.read(configName);
    }
    
    @Override
    public List<DbConfigEntity> readAll() {
        return jaasDao.readAll();
    }
    
    @Override
    public void createOrUpdate(DbConfigEntity config) {
        jaasDao.createOrUpdate(config);
        evictDataServiceFor(config.getName());
    }
    
    @Override
    public void delete(DbConfigEntity config) {
        jaasDao.delete(config);
        evictDataServiceFor(config.getName());
    }
}
