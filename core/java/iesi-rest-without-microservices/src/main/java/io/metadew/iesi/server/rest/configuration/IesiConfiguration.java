package io.metadew.iesi.server.rest.configuration;

import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.*;
import io.metadew.iesi.metadata.configuration.script.ScriptConfiguration;
import io.metadew.iesi.metadata.definition.Context;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class IesiConfiguration {

    @Order(1) @Bean
    public FrameworkInstance frameworkInstance(FrameworkInitializationFile frameworkInitializationFile) {
        FrameworkInstance.getInstance().init(frameworkInitializationFile);
        return FrameworkInstance.getInstance();
    }

    @Bean
    public FrameworkExecution frameworkExecution() {
        Context context = new Context("restserver", "");
        FrameworkExecutionContext frameworkExecutionContext = new FrameworkExecutionContext(context);
        FrameworkExecution.getInstance().init(frameworkExecutionContext);
        return FrameworkExecution.getInstance();
    }

    @Bean FrameworkInitializationFile frameworkInitializationFile() {
        return new FrameworkInitializationFile();
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
