package io.metadew.iesi.common.configuration.metadata.policies;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.executionRequests.ExecutionRequestPolicyDefinition;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.scripts.ScriptPolicyDefinition;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.script.Script;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@org.springframework.context.annotation.Configuration
@Log4j2
public class MetadataPolicyConfiguration {
    private static final String POLICIES = "policies";

    private List<ScriptPolicyDefinition> scriptsPolicyDefinitions;
    private List<ExecutionRequestPolicyDefinition> executionRequestsPolicyDefinitions;

    private final Configuration configuration;

    public MetadataPolicyConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @PostConstruct
    private void postConstruct() {
        if (containsConfiguration()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<?, ?> frameworkSettingsConfiguration = (Map<?, ?>) ((Map<?, ?>) this.configuration.getProperties()
                    .get(MetadataConfiguration.configurationKey))
                    .get(POLICIES);

            scriptsPolicyDefinitions = Arrays.asList(objectMapper.convertValue(frameworkSettingsConfiguration.get("scripts"), ScriptPolicyDefinition[].class));
            executionRequestsPolicyDefinitions = Arrays.asList(objectMapper.convertValue(frameworkSettingsConfiguration.get("execution-requests"), ExecutionRequestPolicyDefinition[].class));
        }
    }

    private boolean containsConfiguration() {
        return configuration.getProperties().containsKey(MetadataConfiguration.configurationKey) &&
                (configuration.getProperties().get(MetadataConfiguration.configurationKey) instanceof Map) &&
                ((Map<?, ?>) configuration.getProperties().get(MetadataConfiguration.configurationKey)).containsKey(POLICIES) &&
                ((Map<?, ?>) configuration.getProperties().get(MetadataConfiguration.configurationKey)).get(POLICIES) instanceof Map;
    }

    public void verifyScriptPolicies(Script script) {
        for (ScriptPolicyDefinition scriptPolicyDefinition : scriptsPolicyDefinitions) {
            scriptPolicyDefinition.verify(script);
        }
    }

    public void verifyExecutionRequestPolicies(ExecutionRequest executionRequest) {
        for (ExecutionRequestPolicyDefinition executionRequestPolicyDefinition : executionRequestsPolicyDefinitions) {
            executionRequestPolicyDefinition.verify(executionRequest);
        }
    }

    public List<ScriptPolicyDefinition> getScriptsPolicyDefinitions() {
        return scriptsPolicyDefinitions;
    }

    public List<ExecutionRequestPolicyDefinition> getExecutionRequestsPolicyDefinitions() {
        return executionRequestsPolicyDefinitions;
    }
}
