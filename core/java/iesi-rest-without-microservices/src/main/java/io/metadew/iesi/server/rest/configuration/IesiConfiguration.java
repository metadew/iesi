package io.metadew.iesi.server.rest.configuration;

import io.metadew.iesi.common.FrameworkInstance;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.common.configuration.metadata.repository.MetadataRepositoryConfiguration;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.environment.EnvironmentConfiguration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionConfiguration;
import io.metadew.iesi.metadata.configuration.execution.script.ScriptExecutionRequestConfiguration;
import io.metadew.iesi.metadata.configuration.impersonation.ImpersonationConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.configuration.script.result.ScriptResultConfiguration;
import io.metadew.iesi.metadata.configuration.user.UserConfiguration;
import io.metadew.iesi.metadata.service.user.AuthorityService;
import io.metadew.iesi.metadata.service.user.GroupService;
import io.metadew.iesi.metadata.service.user.UserService;
import io.metadew.iesi.runtime.ExecutionRequestExecutorService;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
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
    public GroupService groupService() {
        return GroupService.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public AuthorityService authorityService() {
        return AuthorityService.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ComponentConfiguration componentConfiguration() {
        return ComponentConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ExecutionRequestConfiguration executionRequestConfiguration() {
        return ExecutionRequestConfiguration.getInstance();
    }

    @Bean
    public DataSource executionDataSource(MetadataRepositoryConfiguration metadataRepositoryConfiguration) {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        Database database = metadataRepositoryConfiguration.getExecutionServerMetadataRepository().getRepositoryCoordinator().getDatabases().values().stream()
                .findFirst()
                .orElseThrow(RuntimeException::new);
        dataSourceBuilder.url(database.getDatabaseConnection().getConnectionURL());
        dataSourceBuilder.username(database.getDatabaseConnection().getUserName());
        dataSourceBuilder.password(database.getDatabaseConnection().getUserPassword());
        return dataSourceBuilder.build();
    }

    //@Bean
    //public JdbcTemplate executionJdbcTemplate(@Qualifier("executionDataSource") DataSource executionDataSource) {
    //    return new JdbcTemplate(executionDataSource);
    //}

    @Bean
    NamedParameterJdbcTemplate executionJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

}
