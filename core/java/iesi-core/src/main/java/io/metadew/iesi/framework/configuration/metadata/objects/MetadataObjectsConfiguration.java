package io.metadew.iesi.framework.configuration.metadata.objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.configuration.Configuration;
import io.metadew.iesi.framework.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.metadata.definition.MetadataObject;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
public class MetadataObjectsConfiguration {

    private static MetadataObjectsConfiguration INSTANCE;
    private static final String metadataTableKey = "objects";

    private Map<String, MetadataObject> metadataObjectMap;

    public synchronized static MetadataObjectsConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataObjectsConfiguration();
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private MetadataObjectsConfiguration() {
        metadataObjectMap = new HashMap<>();
        if (containsConfiguration()) {
            Map<String, Object> frameworkSettingConfigurations = (Map<String, Object>) ((Map<String, Object>) Configuration.getInstance().getProperties()
                    .get(MetadataConfiguration.configurationKey))
                    .get(metadataTableKey);
            ObjectMapper objectMapper = new ObjectMapper();
            for (Map.Entry<String, Object> entry : frameworkSettingConfigurations.entrySet()) {
                metadataObjectMap.put(entry.getKey(), objectMapper.convertValue(entry.getValue(), MetadataObject.class));
            }
        } else {
            log.warn("no metadata table configurations found on system variable, classpath or filesystem");

        }
    }

    @SuppressWarnings("unchecked")
    private boolean containsConfiguration() {
        return Configuration.getInstance().getProperties().containsKey(MetadataConfiguration.configurationKey) ||
                (Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey) instanceof Map) ||
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).containsKey(metadataTableKey) ||
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).get(metadataTableKey) instanceof Map;
    }
}
