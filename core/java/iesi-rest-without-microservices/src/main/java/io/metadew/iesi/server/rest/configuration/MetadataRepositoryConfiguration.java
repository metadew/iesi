package io.metadew.iesi.server.rest.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;

@Configuration
public class MetadataRepositoryConfiguration {

    @Bean
    @DependsOn("frameworkInstance")
    @Order(0)
    public io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration metadataRepositoryConfiguration() {
        return io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration.getInstance();
    }


}
