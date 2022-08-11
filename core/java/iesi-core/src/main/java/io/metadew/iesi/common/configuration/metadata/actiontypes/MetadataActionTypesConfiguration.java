package io.metadew.iesi.common.configuration.metadata.actiontypes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.metadata.definition.action.type.ActionType;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@org.springframework.context.annotation.Configuration
public class MetadataActionTypesConfiguration {


    private static final String actionsKey = "action-types";
    private Map<String, ActionType> actionTypeMap;

    private final Configuration configuration;

    public MetadataActionTypesConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    private void postConstruct(Configuration configuration) {
        actionTypeMap = new HashMap<>();
        if (containsConfiguration()) {
            Map<String, Object> frameworkSettingConfigurations = (Map<String, Object>) ((Map<String, Object>) this.configuration.getProperties()
                    .get(MetadataConfiguration.configurationKey))
                    .get(actionsKey);
            ObjectMapper objectMapper = new ObjectMapper();
            for (Map.Entry<String, Object> entry : frameworkSettingConfigurations.entrySet()) {
                actionTypeMap.put(entry.getKey(), objectMapper.convertValue(entry.getValue(), ActionType.class));
            }
        } else {
            log.warn("no action type configurations found on system variable, classpath or filesystem");
        }
    }

    public Optional<ActionType> getActionType(String actionType) {
        return Optional.ofNullable(actionTypeMap.get(actionType));
    }

    public Map<String, ActionType> getActionTypes() {
        return actionTypeMap;
    }

    @SuppressWarnings("unchecked")
    private boolean containsConfiguration() {
        return configuration.getProperties().containsKey(MetadataConfiguration.configurationKey) &&
                (configuration.getProperties().get(MetadataConfiguration.configurationKey) instanceof Map) &&
                ((Map<String, Object>) configuration.getProperties().get(MetadataConfiguration.configurationKey)).containsKey(actionsKey) &&
                ((Map<String, Object>) configuration.getProperties().get(MetadataConfiguration.configurationKey)).get(actionsKey) instanceof Map;
    }
}
