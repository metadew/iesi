package io.metadew.iesi.server.rest.configuration;

import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationHandler;
import io.metadew.iesi.datatypes.dataset.implementation.IDatasetImplementationHandler;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.database.IDatabaseDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.in.memory.IInMemoryDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.in.memory.InMemoryDatasetImplementationService;
import io.metadew.iesi.datatypes.template.TemplateService;
import io.metadew.iesi.metadata.repository.DataMetadataRepository;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import io.metadew.iesi.metadata.service.template.ITemplateService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.text.MessageFormat;

@Configuration
@Log4j2
@DependsOn("configuration")
public class IesiConfiguration {

    private final io.metadew.iesi.common.configuration.Configuration configuration;

    public IesiConfiguration(io.metadew.iesi.common.configuration.Configuration configuration) {
        this.configuration = configuration;
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ITemplateService templateService() {
        return TemplateService.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public IDatabaseDatasetImplementationService datasetImplementationService() {
        return DatabaseDatasetImplementationService.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public IInMemoryDatasetImplementationService inMemoryDatasetImplementationService(){
        return InMemoryDatasetImplementationService.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public IDatasetImplementationHandler datasetImplementationHandler(){
        return DatasetImplementationHandler.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ThreadPoolTaskExecutor executionRequestTaskExecutor() {
        int threadSize = configuration
                .getProperty("iesi.server.threads.size")
                .map(Integer.class::cast)
                .orElse(4);
        log.info(MessageFormat.format("starting listener with thread pool size {0}", threadSize));

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadSize);
        executor.setMaxPoolSize(threadSize);
        executor.setThreadNamePrefix("executionRequestTaskExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean
    @DependsOn("frameworkInstance")
    @Profile("!test & !sqlite")
    public DataSource dataSource(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        DataMetadataRepository dataMetadataRepository = metadataRepositoryConfiguration.getDataMetadataRepository();
        RepositoryCoordinator repositoryCoordinator = dataMetadataRepository.getRepositoryCoordinator();
        Database database = repositoryCoordinator.getDatabases().get("owner");
        return database.getConnectionPool();
    }
}
