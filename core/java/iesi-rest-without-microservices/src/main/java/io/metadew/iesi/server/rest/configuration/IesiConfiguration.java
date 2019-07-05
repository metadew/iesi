package io.metadew.iesi.server.rest.configuration;

import io.metadew.iesi.framework.definition.FrameworkInitializationFile;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkExecutionContext;
import io.metadew.iesi.framework.instance.FrameworkInstance;
import io.metadew.iesi.metadata.configuration.*;
import io.metadew.iesi.metadata.definition.Context;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IesiConfiguration {

    @Bean
    public FrameworkExecution frameworkExecution(FrameworkInstance frameworkInstance, FrameworkInitializationFile frameworkInitializationFile) {
        Context context = new Context();
        context.setName("restserver");
        context.setScope("");
        FrameworkExecutionContext frameworkExecutionContext = new FrameworkExecutionContext(context);
        return new FrameworkExecution(frameworkInstance, frameworkExecutionContext, frameworkInitializationFile);
    }

    @Bean
    public FrameworkInstance frameworkInstance(FrameworkInitializationFile frameworkInitializationFile) {
        return new FrameworkInstance(frameworkInitializationFile);
    }

    @Bean FrameworkInitializationFile frameworkInitializationFile() {
        FrameworkInitializationFile frameworkInitializationFile = new FrameworkInitializationFile();
        frameworkInitializationFile.setName("");
        return frameworkInitializationFile;
    }

    @Bean
    public ConnectionConfiguration connectionConfiguration(FrameworkInstance frameworkInstance) {
        return new ConnectionConfiguration(frameworkInstance);
    }

    @Bean
    public EnvironmentConfiguration environmentConfiguration(FrameworkInstance frameworkInstance) {
        return new EnvironmentConfiguration(frameworkInstance);
    }

    @Bean
    public ImpersonationConfiguration impersonationConfiguration(FrameworkInstance frameworkInstance) {
        return new ImpersonationConfiguration(frameworkInstance);
    }


    @Bean
    public ScriptConfiguration scriptConfiguration(FrameworkInstance frameworkInstance) {
        return new ScriptConfiguration(frameworkInstance);
    }

    @Bean
    public UserConfiguration userConfiguration(FrameworkInstance frameworkInstance) {
        return new UserConfiguration(frameworkInstance);
    }

    @Bean
    public ComponentConfiguration componentConfiguration(FrameworkInstance frameworkInstance) {
        return new ComponentConfiguration(frameworkInstance);
    }

}
