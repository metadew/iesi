package io.metadew.iesi;

import io.metadew.iesi.common.FrameworkControl;
import io.metadew.iesi.common.configuration.metadata.objects.MetadataObjectsConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryService;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinatorHandler;
import io.metadew.iesi.common.configuration.metadata.tables.MetadataTablesConfiguration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.database.DatabaseHandler;
import io.metadew.iesi.metadata.service.metadata.MetadataTableService;
import org.junit.jupiter.api.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestConfiguration {

    @Bean
    @Order(0)
    public SpringContext springContext() {
        return new SpringContext();
    }

    @Bean
    @DependsOn("springContext")
    public io.metadew.iesi.common.configuration.Configuration configuration() {
        return new io.metadew.iesi.common.configuration.Configuration();
    }

    @Bean
    public MetadataRepositoryCoordinatorHandler metadataRepositoryCoordinatorHandler() {
        return new MetadataRepositoryCoordinatorHandler();
    }

    @Bean
    public MetadataRepositoryService metadataRepositoryService(MetadataRepositoryCoordinatorHandler metadataRepositoryCoordinatorHandler) {
        return new MetadataRepositoryService(metadataRepositoryCoordinatorHandler);
    }

    @Bean(initMethod = "createAllTables", destroyMethod = "dropAllTables")
    @DependsOn({ "metadataTablesConfiguration", "metadataObjectsConfiguration"})
    public MetadataRepositoryConfiguration metadataRepositoryConfiguration(io.metadew.iesi.common.configuration.Configuration configuration, MetadataRepositoryService metadataRepositoryService) {
        return new MetadataRepositoryConfiguration(configuration, metadataRepositoryService);
    }

    @Bean
    public MetadataTablesConfiguration metadataTablesConfiguration(io.metadew.iesi.common.configuration.Configuration configuration) {
        return new MetadataTablesConfiguration(configuration);
    }

    @Bean
    public MetadataTableService metadataTableService(MetadataTablesConfiguration metadataTablesConfiguration) {
        return new MetadataTableService(metadataTablesConfiguration);
    }

    @Bean
    public MetadataObjectsConfiguration metadataObjectsConfiguration(io.metadew.iesi.common.configuration.Configuration configuration) {
        return new MetadataObjectsConfiguration(configuration);
    }

    @Bean
    public FrameworkCrypto frameworkCrypto(io.metadew.iesi.common.configuration.Configuration configuration) {
        return new FrameworkCrypto(configuration);
    }

    @Bean
    public FrameworkControl frameworkControl(io.metadew.iesi.common.configuration.Configuration configuration) {
        return new FrameworkControl(configuration);
    }

    @Bean
    public DatabaseHandler databaseHandler(FrameworkControl frameworkControl, FrameworkCrypto frameworkCrypto) {
        return new DatabaseHandler(frameworkControl, frameworkCrypto);
    }
}
