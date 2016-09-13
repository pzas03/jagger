package com.griddynamics.jagger.jaas.config;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.engine.e1.services.DefaultDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

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
