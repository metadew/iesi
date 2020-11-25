package io.metadew.iesi.server.rest.configuration;

import io.metadew.iesi.common.FrameworkInstance;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetService;
import io.metadew.iesi.datatypes.dataset.IDatasetService;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.IDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationService;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.environment.EnvironmentConfiguration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionConfiguration;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionRequestConfiguration;
import io.metadew.iesi.metadata.configuration.impersonation.ImpersonationConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
import io.metadew.iesi.metadata.configuration.security.SecurityGroupConfiguration;
import io.metadew.iesi.metadata.configuration.user.TeamConfiguration;
import io.metadew.iesi.metadata.configuration.user.UserConfiguration;
import io.metadew.iesi.metadata.definition.security.SecurityGroup;
import io.metadew.iesi.metadata.service.security.SecurityGroupService;
import io.metadew.iesi.metadata.service.user.RoleService;
import io.metadew.iesi.metadata.service.user.TeamService;
import io.metadew.iesi.metadata.service.user.UserService;
import io.metadew.iesi.runtime.ExecutionRequestExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;

import java.io.IOException;

@Configuration
public class IesiConfiguration {

    @Bean
    @Order(0)
    public FrameworkInstance frameworkInstance() throws IOException {
        io.metadew.iesi.common.configuration.Configuration.getInstance();
        MetadataConfiguration.getInstance();
        return FrameworkInstance.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    @Order(0)
    public MetadataRepositoryConfiguration metadataRepositoryConfiguration() {
        return MetadataRepositoryConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    ExecutionRequestExecutorService executorService() {
        return ExecutionRequestExecutorService.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ConnectionConfiguration connectionConfiguration() {
        return ConnectionConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public EnvironmentConfiguration environmentConfiguration() {
        return EnvironmentConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ImpersonationConfiguration impersonationConfiguration() {
        return ImpersonationConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ScriptConfiguration scriptConfiguration() {
        return ScriptConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ScriptResultConfiguration scriptResultConfiguration() {
        return ScriptResultConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ScriptExecutionRequestConfiguration scriptExecutionRequestConfiguration() {
        return ScriptExecutionRequestConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ScriptExecutionConfiguration scriptExecutionConfiguration() {
        return ScriptExecutionConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public UserConfiguration userConfiguration() {
        return UserConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public UserService userService() {
        return UserService.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public TeamService teamService() {
        return TeamService.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public RoleService roleService() {
        return RoleService.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public SecurityGroupService securityGroupService() {
        return SecurityGroupService.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public TeamConfiguration teamConfiguration() {
        return TeamConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public SecurityGroupConfiguration securityGroupConfiguration() {
        return SecurityGroupConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ComponentConfiguration componentConfiguration() {
        return ComponentConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public DatasetConfiguration datasetConfiguration() {
        return DatasetConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public IDatasetService datasetService() {
        return DatasetService.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public DatasetImplementationConfiguration datasetImplementationConfiguration() {
        return DatasetImplementationConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public IDatasetImplementationService datasetImplementationService() {
        return InMemoryDatasetImplementationService.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ExecutionRequestConfiguration executionRequestConfiguration() {
        return ExecutionRequestConfiguration.getInstance();
    }

}
