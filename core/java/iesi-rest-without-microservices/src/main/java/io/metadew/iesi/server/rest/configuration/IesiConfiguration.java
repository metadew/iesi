package io.metadew.iesi.server.rest.configuration;

import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.UserConfiguration;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.environment.EnvironmentConfiguration;
import io.metadew.iesi.metadata.configuration.execution.ExecutionRequestConfiguration;
import io.metadew.iesi.metadata.configuration.impersonation.ImpersonationConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.Context;
import io.metadew.iesi.runtime.ExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;

import java.sql.SQLException;

@Configuration
public class IesiConfiguration {

    @Bean
    @Order(0)
    public FrameworkInstance frameworkInstance(FrameworkInitializationFile frameworkInitializationFile, FrameworkExecutionContext frameworkExecutionContext) throws SQLException {
        FrameworkInstance.getInstance().init(frameworkInitializationFile, frameworkExecutionContext);
        return FrameworkInstance.getInstance();
    }

    @Bean
    FrameworkExecutionContext frameworkExecutionContext() {
        return new FrameworkExecutionContext(new Context("restserver", ""));
    }

    @Bean FrameworkInitializationFile frameworkInitializationFile() {
        return new FrameworkInitializationFile(System.getProperty("iesi.ini", "iesi-conf.ini"));
    }

    @Bean
    @DependsOn("frameworkInstance")
    ExecutorService executorService() {
        return ExecutorService.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ConnectionConfiguration connectionConfiguration() {
        return new ConnectionConfiguration();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public EnvironmentConfiguration environmentConfiguration() {
        return new EnvironmentConfiguration();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ImpersonationConfiguration impersonationConfiguration() {
        return new ImpersonationConfiguration();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ScriptConfiguration scriptConfiguration() {
        return new ScriptConfiguration();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public UserConfiguration userConfiguration() {
        return new UserConfiguration();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ComponentConfiguration componentConfiguration() {
        return new ComponentConfiguration();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ExecutionRequestConfiguration executionRequestConfiguration() {
        return new ExecutionRequestConfiguration();
    }

}
