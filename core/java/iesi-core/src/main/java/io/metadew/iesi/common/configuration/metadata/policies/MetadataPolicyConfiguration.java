package io.metadew.iesi.common.configuration.metadata.policies;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.executionRequests.ExecutionRequestPolicyDefinition;
import io.metadew.iesi.common.configuration.metadata.policies.definitions.scripts.ScriptPolicyDefinition;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Log4j2
public class MetadataPolicyConfiguration {
    private static MetadataPolicyConfiguration instance;
    private static final String policiesKey = "policies";

    private List<ScriptPolicyDefinition> scriptsPolicyDefinitions;
    private List<ExecutionRequestPolicyDefinition> executionRequestsPolicyDefinitions;

    public synchronized static MetadataPolicyConfiguration getInstance() {
        if (instance == null) {
            instance = new MetadataPolicyConfiguration();
        }
        return instance;
    }

    private MetadataPolicyConfiguration() {
        if (containsConfiguration()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> frameworkSettingsConfiguration = (Map<String, Object>) ((Map<String, Object>) Configuration.getInstance().getProperties()
                    .get(MetadataConfiguration.configurationKey))
                    .get(policiesKey);

            scriptsPolicyDefinitions = Arrays.asList(objectMapper.convertValue(frameworkSettingsConfiguration.get("scripts"), ScriptPolicyDefinition[].class));
        }
    }

    private boolean containsConfiguration() {
        return Configuration.getInstance().getProperties().containsKey(MetadataConfiguration.configurationKey) &&
                (Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey) instanceof Map) &&
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).containsKey(policiesKey) &&
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).get(policiesKey) instanceof Map;
    }

    public List<ScriptPolicyDefinition> getScriptsPolicyDefinitions() {
        return scriptsPolicyDefinitions;
    }

    public List<ExecutionRequestPolicyDefinition> getExecutionRequestsPolicyDefinitions() {
        return executionRequestsPolicyDefinitions;
    }
}
