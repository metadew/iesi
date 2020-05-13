package io.metadew.iesi.common.configuration.metadata.connectiontypes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.metadata.definition.connection.ConnectionType;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
public class MetadataConnectionTypesConfiguration {

    private static MetadataConnectionTypesConfiguration INSTANCE;
    private static final String actionsKey = "connection-types";

    private Map<String, ConnectionType> componentTypeMap;

    public synchronized static MetadataConnectionTypesConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataConnectionTypesConfiguration();
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private MetadataConnectionTypesConfiguration() {
        componentTypeMap = new HashMap<>();
        if (containsConfiguration()) {
            Map<String, Object> frameworkSettingConfigurations = (Map<String, Object>) ((Map<String, Object>) Configuration.getInstance().getProperties()
                    .get(MetadataConfiguration.configurationKey))
                    .get(actionsKey);
            ObjectMapper objectMapper = new ObjectMapper();
            for (Map.Entry<String, Object> entry : frameworkSettingConfigurations.entrySet()) {
                componentTypeMap.put(entry.getKey(), objectMapper.convertValue(entry.getValue(), ConnectionType.class));
            }
        } else {
            log.warn("no connection type configurations found on system variable, classpath or filesystem");

        }
    }

    public Optional<ConnectionType> getConnectionType(String connectionType) {
        return Optional.ofNullable(componentTypeMap.get(connectionType));
    }

    @SuppressWarnings("unchecked")
    private boolean containsConfiguration() {
        return Configuration.getInstance().getProperties().containsKey(MetadataConfiguration.configurationKey) &&
                (Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey) instanceof Map) &&
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).containsKey(actionsKey) &&
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).get(actionsKey) instanceof Map;
    }
}
