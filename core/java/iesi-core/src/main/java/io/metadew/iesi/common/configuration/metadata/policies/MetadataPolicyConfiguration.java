package io.metadew.iesi.common.configuration.metadata.policies;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.PolicyVerificationException;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.executionRequests.ExecutionRequestLabelPolicy;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.executionRequests.ExecutionRequestPolicyDefinition;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.scripts.ScriptPolicyDefinition;
import io.metadew.iesi.metadata.definition.execution.ExecutionRequest;
import io.metadew.iesi.metadata.definition.script.Script;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Log4j2
public class MetadataPolicyConfiguration {
    private static MetadataPolicyConfiguration instance;
    private static final String POLICIES = "policies";

    private List<ScriptPolicyDefinition> scriptsPolicyDefinitions;
    private List<ExecutionRequestPolicyDefinition> executionRequestsPolicyDefinitions;

    public static synchronized MetadataPolicyConfiguration getInstance() {
        if (instance == null) {
            instance = new MetadataPolicyConfiguration();
        }
        return instance;
    }

    private MetadataPolicyConfiguration() {
        if (containsConfiguration()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<?, ?> frameworkSettingsConfiguration = (Map<?, ?>) ((Map<?, ?>) Configuration.getInstance().getProperties()
                    .get(MetadataConfiguration.configurationKey))
                    .get(POLICIES);

            scriptsPolicyDefinitions = Arrays.asList(objectMapper.convertValue(frameworkSettingsConfiguration.get("scripts"), ScriptPolicyDefinition[].class));
            executionRequestsPolicyDefinitions = Arrays.asList(objectMapper.convertValue(frameworkSettingsConfiguration.get("execution-requets"), ExecutionRequestPolicyDefinition[].class));
        }
    }

    private boolean containsConfiguration() {
        return Configuration.getInstance().getProperties().containsKey(MetadataConfiguration.configurationKey) &&
                (Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey) instanceof Map) &&
                ((Map<?, ?>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).containsKey(POLICIES) &&
                ((Map<?, ?>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).get(POLICIES) instanceof Map;
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
