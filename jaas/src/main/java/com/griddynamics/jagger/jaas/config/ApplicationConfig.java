package com.griddynamics.jagger.jaas.config;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.engine.e1.services.DefaultDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.annotation.Bean;


@PropertySources({@PropertySource("classpath:/basic/default.environment.properties")})
@ImportResource({"classpath:/common/storage.rdb.client.conf.xml", "classpath:/spring/dbapi.config.xml"})
@Configuration
public class ApplicationConfig {

    @Autowired
    DatabaseService databaseService;


    @Bean
    public DataService getDataService() {
        return new DefaultDataService(databaseService);
    }
}
