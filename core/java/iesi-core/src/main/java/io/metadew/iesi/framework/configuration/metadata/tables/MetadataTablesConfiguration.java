package io.metadew.iesi.framework.configuration.metadata.tables;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.configuration.Configuration;
import io.metadew.iesi.framework.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.metadata.definition.MetadataTable;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
public class MetadataTablesConfiguration {

    private static MetadataTablesConfiguration INSTANCE;
    private static final String metadataTableKey = "tables";

    private Map<String, MetadataTable> metadataTableMap;

    public synchronized static MetadataTablesConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataTablesConfiguration();
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private MetadataTablesConfiguration() {
        metadataTableMap = new HashMap<>();
        if (containsConfiguration()) {
            Map<String, Object> frameworkSettingConfigurations = (Map<String, Object>) ((Map<String, Object>) Configuration.getInstance().getProperties()
                    .get(MetadataConfiguration.configurationKey))
                    .get(metadataTableKey);
            ObjectMapper objectMapper = new ObjectMapper();
            for (Map.Entry<String, Object> entry : frameworkSettingConfigurations.entrySet()) {
                metadataTableMap.put(entry.getKey(), objectMapper.convertValue(entry.getValue(), MetadataTable.class));
            }
        } else {
            log.warn("no metadata table configurations found on system variable, classpath or filesystem");

        }
    }

    public Optional<MetadataTable> getMetadataTable(String metadataTable) {
        return Optional.ofNullable(metadataTableMap.get(metadataTable));
    }

    @SuppressWarnings("unchecked")
    private boolean containsConfiguration() {
        return Configuration.getInstance().getProperties().containsKey(MetadataConfiguration.configurationKey) ||
                (Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey) instanceof Map) ||
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).containsKey(metadataTableKey) ||
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).get(metadataTableKey) instanceof Map;
    }
}
