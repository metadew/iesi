package io.metadew.iesi.server.rest.configuration;

import io.metadew.iesi.common.FrameworkInstance;
import io.metadew.iesi.common.configuration.guard.GuardConfiguration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.datatypes.dataset.DatasetConfiguration;
import io.metadew.iesi.datatypes.dataset.DatasetService;
import io.metadew.iesi.datatypes.dataset.IDatasetService;
import io.metadew.iesi.datatypes.dataset.implementation.DatasetImplementationConfiguration;
import io.metadew.iesi.datatypes.dataset.implementation.IDatasetImplementationService;
import io.metadew.iesi.datatypes.dataset.implementation.inmemory.InMemoryDatasetImplementationService;
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
import io.metadew.iesi.metadata.service.security.SecurityGroupService;
import io.metadew.iesi.metadata.service.user.RoleService;
import io.metadew.iesi.metadata.service.user.TeamService;
import io.metadew.iesi.metadata.service.user.UserService;
import io.metadew.iesi.openapi.OpenAPIGenerator;
import io.metadew.iesi.runtime.script.ScriptExecutorService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.text.MessageFormat;

@org.springframework.context.annotation.Configuration
@Log4j2
public class IesiConfiguration {


    @Value("${spring.ldap.server.url: 'ldap://localhost:8389/dc=springframework,dc=org'}")
    private String url;

    @Bean
    @Order(0)
    public FrameworkInstance frameworkInstance() throws IOException {
        io.metadew.iesi.common.configuration.Configuration.getInstance();
        MetadataConfiguration.getInstance();
        return FrameworkInstance.getInstance();
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

    @Bean("rawUserService")
    @DependsOn("frameworkInstance")
    public UserService userService() {
        return UserService.getInstance();
    }

    @Bean("rawTeamService")
    @DependsOn("frameworkInstance")
    public TeamService teamService() {
        return TeamService.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public RoleService roleService() {
        return RoleService.getInstance();
    }

    @Bean("rawSecurityGroupService")
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

    @Bean
    @DependsOn("frameworkInstance")
    public ScriptResultOutputConfiguration scriptResultOutputConfiguration() {
        return ScriptResultOutputConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ActionResultConfiguration actionResultConfiguration() {
        return ActionResultConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ActionResultOutputConfiguration actionResultOutputConfiguration() {
        return ActionResultOutputConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ActionDesignTraceConfiguration actionDesignTraceConfiguration() {
        return ActionDesignTraceConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ActionParameterDesignTraceConfiguration actionParameterDesignTraceConfiguration() {
        return ActionParameterDesignTraceConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ActionParameterTraceConfiguration actionParameterTraceConfiguration() {
        return ActionParameterTraceConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ScriptLabelDesignTraceConfiguration scriptLabelDesignTraceConfiguration() {
        return ScriptLabelDesignTraceConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public OpenAPIGenerator openAPIGenerator() {
        return OpenAPIGenerator.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ScriptDesignAuditConfiguration scriptDesignAuditConfiguration(){
        return ScriptDesignAuditConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public GuardConfiguration guardConfiguration(){
        return GuardConfiguration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public ThreadPoolTaskExecutor executionRequestTaskExecutor() {
        int threadSize = io.metadew.iesi.common.configuration.Configuration.getInstance()
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

    @Bean("iesiProperties")
    @DependsOn("frameworkInstance")
    public io.metadew.iesi.common.configuration.Configuration iesiProperties() {
        return io.metadew.iesi.common.configuration.Configuration.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public FrameworkCrypto frameworkCrypto(){
        return FrameworkCrypto.getInstance();
    }

    @Bean
    @Profile("single_process")
    @DependsOn("frameworkInstance")
    public ScriptExecutorService scriptExecutorService() {
        return ScriptExecutorService.getInstance();
    }

    @Bean
    @DependsOn("frameworkInstance")
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(url);
        contextSource.setAnonymousReadOnly(true);
        /*contextSource.setBase((String) Configuration.getInstance().getMandatoryProperty("iesi.ldap.partitionSuffix"));*/
        contextSource.setUserDn("uid={0},ou=people");
        contextSource.afterPropertiesSet();
        /*contextSource.setPassword((String) Configuration.getInstance().getMandatoryProperty("iesi.ldap.password"));*/
        return contextSource;
    }

    @Bean
    @DependsOn("frameworkInstance")
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(contextSource());
    }

}
