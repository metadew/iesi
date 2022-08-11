package io.metadew.iesi.common.configuration.metadata.componenttypes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.metadata.definition.component.ComponentType;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@org.springframework.context.annotation.Configuration
public class MetadataComponentTypesConfiguration {

    private static final String actionsKey = "component-types";

    private Map<String, ComponentType> componentTypeMap;

    private final Configuration configuration;

    public MetadataComponentTypesConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }


    @SuppressWarnings("unchecked")
    @PostConstruct
    private void postConstruct(Configuration configuration) {
        componentTypeMap = new HashMap<>();
        if (containsConfiguration()) {
            Map<String, Object> frameworkSettingConfigurations = (Map<String, Object>) ((Map<String, Object>) this.configuration.getProperties()
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

    public Map<String, ComponentType> getComponentTypes() {
        return componentTypeMap;
    }

    @SuppressWarnings("unchecked")
    private boolean containsConfiguration() {
        return configuration.getProperties().containsKey(MetadataConfiguration.configurationKey) &&
                (configuration.getProperties().get(MetadataConfiguration.configurationKey) instanceof Map) &&
                ((Map<String, Object>) configuration.getProperties().get(MetadataConfiguration.configurationKey)).containsKey(actionsKey) &&
                ((Map<String, Object>) configuration.getProperties().get(MetadataConfiguration.configurationKey)).get(actionsKey) instanceof Map;
    }
}
