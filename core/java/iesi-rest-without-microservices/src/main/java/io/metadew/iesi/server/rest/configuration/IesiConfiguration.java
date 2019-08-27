package io.metadew.iesi.server.rest.configuration;

import io.metadew.iesi.framework.definition.Framework;
import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.*;
import io.metadew.iesi.metadata.configuration.component.ComponentConfiguration;
import io.metadew.iesi.metadata.configuration.connection.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.environment.EnvironmentConfiguration;
import io.metadew.iesi.metadata.configuration.impersonation.ImpersonationConfiguration;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.Context;
import io.metadew.iesi.runtime.ExecutorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class IesiConfiguration {

    @Order(1) @Bean
    public FrameworkInstance frameworkInstance(FrameworkInitializationFile frameworkInitializationFile, FrameworkExecutionContext frameworkExecutionContext) {
        FrameworkInstance.getInstance().init(frameworkInitializationFile, frameworkExecutionContext);
        return FrameworkInstance.getInstance();
    }

    @Bean
    public FrameworkExecution frameworkExecution(FrameworkExecutionContext frameworkExecutionContext) {
        FrameworkExecution.getInstance().init(frameworkExecutionContext);
        return FrameworkExecution.getInstance();
    }

    @Bean
    FrameworkExecutionContext frameworkExecutionContext() {
        return new FrameworkExecutionContext(new Context("restserver", ""));
    }

    @Bean FrameworkInitializationFile frameworkInitializationFile() {
        return new FrameworkInitializationFile();
    }

    @Bean
    ExecutorService executorService() {
        return ExecutorService.getInstance();
    }

    @Bean
    public ConnectionConfiguration connectionConfiguration() {
        return new ConnectionConfiguration();
    }

    @Bean
    public EnvironmentConfiguration environmentConfiguration() {
        return new EnvironmentConfiguration();
    }

    @Bean
    public ImpersonationConfiguration impersonationConfiguration() {
        return new ImpersonationConfiguration();
    }


    @Bean
    public ScriptConfiguration scriptConfiguration() {
        return new ScriptConfiguration();
    }

    @Bean
    public UserConfiguration userConfiguration() {
        return new UserConfiguration();
    }

    @Bean
    public ComponentConfiguration componentConfiguration() {
        return new ComponentConfiguration();
    }

}
