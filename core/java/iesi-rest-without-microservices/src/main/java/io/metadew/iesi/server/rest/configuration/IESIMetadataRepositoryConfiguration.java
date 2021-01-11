package io.metadew.iesi.server.rest.configuration;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;

@Configuration
public class IESIMetadataRepositoryConfiguration {

    @Bean
    @DependsOn("frameworkInstance")
    @Order(0)
    public MetadataRepositoryConfiguration metadataRepositoryConfiguration() {
        return MetadataRepositoryConfiguration.getInstance();
    }


}
