package io.metadew.iesi.common.configuration.metadata.componenttypes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.metadata.definition.component.ComponentType;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
public class MetadataComponentTypesConfiguration {

    private static MetadataComponentTypesConfiguration INSTANCE;
    private static final String actionsKey = "component-types";

    private Map<String, ComponentType> componentTypeMap;

    public synchronized static MetadataComponentTypesConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataComponentTypesConfiguration();
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private MetadataComponentTypesConfiguration() {
        componentTypeMap = new HashMap<>();
        if (containsConfiguration()) {
            Map<String, Object> frameworkSettingConfigurations = (Map<String, Object>) ((Map<String, Object>) Configuration.getInstance().getProperties()
                    .get(MetadataConfiguration.configurationKey))
                    .get(actionsKey);
            ObjectMapper objectMapper = new ObjectMapper();
            for (Map.Entry<String, Object> entry : frameworkSettingConfigurations.entrySet()) {
                componentTypeMap.put(entry.getKey(), objectMapper.convertValue(entry.getValue(), ComponentType.class));
            }
        } else {
            log.warn("no component type configurations found on system variable, classpath or filesystem");

        }
    }

    public Optional<ComponentType> getComponentType(String componentType) {
        return Optional.ofNullable(componentTypeMap.get(componentType));
    }

    @SuppressWarnings("unchecked")
    private boolean containsConfiguration() {
        return Configuration.getInstance().getProperties().containsKey(MetadataConfiguration.configurationKey) ||
                (Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey) instanceof Map) ||
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).containsKey(actionsKey) ||
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).get(actionsKey) instanceof Map;
    }
}
