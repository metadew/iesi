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
    public ConnectionConfiguration connectionConfiguration(FrameworkExecution frameworkExecution) {
        return new ConnectionConfiguration(frameworkExecution);
    }

    @Bean
    public EnvironmentConfiguration environmentConfiguration(FrameworkExecution frameworkExecution) {
        return new EnvironmentConfiguration(frameworkExecution);
    }

    @Bean
    public ImpersonationConfiguration impersonationConfiguration(FrameworkExecution frameworkExecution) {
        return new ImpersonationConfiguration(frameworkExecution);
    }


    @Bean
    public ScriptConfiguration scriptConfiguration(FrameworkExecution frameworkExecution) {
        return new ScriptConfiguration(frameworkExecution);
    }

    @Bean
    public UserConfiguration userConfiguration(FrameworkExecution frameworkExecution) {
        return new UserConfiguration(frameworkExecution);
    }

    @Bean
    public ComponentConfiguration componentConfiguration(FrameworkExecution frameworkExecution) {
        return new ComponentConfiguration(frameworkExecution);
    }

}
