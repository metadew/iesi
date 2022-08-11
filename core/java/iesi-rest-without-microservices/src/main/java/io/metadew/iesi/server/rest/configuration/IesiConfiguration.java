package io.metadew.iesi.server.rest.configuration;

import io.metadew.iesi.common.FrameworkInstance;
import io.metadew.iesi.common.configuration.guard.GuardConfiguration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.common.configuration.metadata.policies.MetadataPolicyConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetService;
import io.metadew.iesi.datatypes.dataset.IDatasetService;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationHandler;
import io.metadew.iesi.datatypes.dataset.implementation.IDatasetImplementationHandler;
import io.metadew.iesi.datatypes.dataset.implementation.database.DatabaseDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.database.IDatabaseDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.in.memory.IInMemoryDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.in.memory.InMemoryDatasetImplementationService;
import io.metadew.iesi.datatypes.template.TemplateService;
import io.metadew.iesi.metadata.configuration.action.design.ActionDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.action.design.ActionParameterDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.action.result.ActionResultConfiguration;
import io.metadew.iesi.metadata.configuration.action.result.ActionResultOutputConfiguration;
import io.metadew.iesi.metadata.configuration.action.trace.ActionParameterTraceConfiguration;
import io.metadew.iesi.metadata.configuration.audit.ScriptDesignAuditConfiguration;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.environment.EnvironmentConfiguration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionConfiguration;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionRequestConfiguration;
import io.metadew.iesi.metadata.configuration.impersonation.ImpersonationConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.configuration.script.design.ScriptLabelDesignTraceConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultOutputConfiguration;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.configuration.user.TeamConfiguration;
import io.metadew.iesi.metadata.configuration.user.UserConfiguration;
import io.metadew.iesi.metadata.repository.DataMetadataRepository;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;
import io.metadew.iesi.metadata.service.security.SecurityGroupService;
import io.metadew.iesi.metadata.service.template.ITemplateService;
import io.metadew.iesi.metadata.service.user.RoleService;
import io.metadew.iesi.metadata.service.user.TeamService;
import io.metadew.iesi.metadata.service.user.UserService;
import io.metadew.iesi.openapi.OpenAPIGenerator;
import io.metadew.iesi.runtime.script.ScriptExecutorService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;
import java.io.IOException;
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
    public GuardConfiguration guardConfiguration(){
        return GuardConfiguration.getInstance();
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
    @Profile("!test")
    public DataSource dataSource(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        DataMetadataRepository dataMetadataRepository = metadataRepositoryConfiguration.getDataMetadataRepository();
        RepositoryCoordinator repositoryCoordinator = dataMetadataRepository.getRepositoryCoordinator();
        Database database = repositoryCoordinator.getDatabases().get("owner");
        return database.getConnectionPool();
    }
}
