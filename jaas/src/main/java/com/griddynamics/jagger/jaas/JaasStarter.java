package com.griddynamics.jagger.jaas;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.engine.e1.services.DefaultDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Spring Boot based starter.
 */
@PropertySources({@PropertySource("classpath:/basic/default.environment.properties")})
@ImportResource({"classpath:/common/storage.rdb.client.conf.xml", "classpath:/spring/dbapi.config.xml"})
@SpringBootApplication
public class JaasStarter {

    @Autowired
    DatabaseService databaseService;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(JaasStarter.class, args);
    }

    @Bean
    public DataService getDataService() {
        return new DefaultDataService(databaseService);
    }
}
