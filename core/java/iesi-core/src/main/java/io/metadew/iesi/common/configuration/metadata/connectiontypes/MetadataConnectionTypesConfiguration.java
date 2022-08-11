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

    private static final String actionsKey = "connection-types";

    private Map<String, ConnectionType> connectionTypeMap;

    private final Configuration configuration;

    @SuppressWarnings("unchecked")
    private MetadataConnectionTypesConfiguration(Configuration configuration) {
        this.configuration = configuration;
        connectionTypeMap = new HashMap<>();
        if (containsConfiguration()) {
            Map<String, Object> frameworkSettingConfigurations = (Map<String, Object>) ((Map<String, Object>) this.configuration.getProperties()
                    .get(MetadataConfiguration.configurationKey))
                    .get(actionsKey);
            ObjectMapper objectMapper = new ObjectMapper();
            for (Map.Entry<String, Object> entry : frameworkSettingConfigurations.entrySet()) {
                connectionTypeMap.put(entry.getKey(), objectMapper.convertValue(entry.getValue(), ConnectionType.class));
            }
        } else {
            log.warn("no connection type configurations found on system variable, classpath or filesystem");

        }
    }

    public Optional<ConnectionType> getConnectionType(String connectionType) {
        return Optional.ofNullable(connectionTypeMap.get(connectionType));
    }

    public Map<String, ConnectionType> getConnectionTypes() {
        return connectionTypeMap;
    }

    @SuppressWarnings("unchecked")
    private boolean containsConfiguration() {
        return configuration.getProperties().containsKey(MetadataConfiguration.configurationKey) &&
                (configuration.getProperties().get(MetadataConfiguration.configurationKey) instanceof Map) &&
                ((Map<String, Object>) configuration.getProperties().get(MetadataConfiguration.configurationKey)).containsKey(actionsKey) &&
                ((Map<String, Object>) configuration.getProperties().get(MetadataConfiguration.configurationKey)).get(actionsKey) instanceof Map;
    }
}
