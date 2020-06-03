package io.metadew.iesi.server.rest.configuration;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.metadata.repository.MetadataRepository;
import org.springframework.context.annotation.*;

@Configuration
@Profile("test")
public class TestConfiguration {

    @Bean
    @Primary
    @DependsOn("frameworkInstance")
    public MetadataRepositoryConfiguration metadataRepositoryConfiguration() {
        MetadataRepositoryConfiguration.getInstance().getMetadataRepositories().forEach(MetadataRepository::createAllTables);
        return MetadataRepositoryConfiguration.getInstance();
    }

}