package io.metadew.iesi.server.rest.configuration;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;

@Configuration
@Profile("test")
public class TestConfiguration {

    @Bean
    @Primary
    @Order(0)
    @DependsOn("frameworkInstance")
    public MetadataRepositoryConfiguration metadataRepositoryConfiguration() {
        MetadataRepositoryConfiguration.getInstance().getMetadataRepositories().forEach(MetadataRepository::createAllTables);
        return MetadataRepositoryConfiguration.getInstance();
    }

}