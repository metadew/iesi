package io.metadew.iesi.framework.configuration.metadata.actiontypes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.configuration.Configuration;
import io.metadew.iesi.framework.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.metadata.definition.action.type.ActionType;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
public class MetadataActionTypesConfiguration {

    private static MetadataActionTypesConfiguration INSTANCE;
    private static final String actionsKey = "action-types";

    private Map<String, ActionType> actionTypeMap;

    public synchronized static MetadataActionTypesConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataActionTypesConfiguration();
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private MetadataActionTypesConfiguration() {
        actionTypeMap = new HashMap<>();
        if (containsConfiguration()) {
            Map<String, Object> frameworkSettingConfigurations = (Map<String, Object>) ((Map<String, Object>) Configuration.getInstance().getProperties()
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

    @SuppressWarnings("unchecked")
    private boolean containsConfiguration() {
        return Configuration.getInstance().getProperties().containsKey(MetadataConfiguration.configurationKey) ||
                (Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey) instanceof Map) ||
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).containsKey(actionsKey) ||
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).get(actionsKey) instanceof Map;
    }
}
