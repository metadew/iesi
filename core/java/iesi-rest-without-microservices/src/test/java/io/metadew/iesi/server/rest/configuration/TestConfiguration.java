package io.metadew.iesi.server.rest.configuration;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;

@Configuration
@Profile("test")
@Log4j2
public class TestConfiguration {

    @Bean(destroyMethod = "dropAllTables", initMethod = "createAllTables")
    @Primary
    @Order(0)
    @DependsOn("frameworkInstance")
    public MetadataRepositoryConfiguration metadataRepositoryConfiguration() {
        return MetadataRepositoryConfiguration.getInstance();
    }
}