package io.metadew.iesi.common.configuration.metadata.objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.metadata.definition.MetadataObject;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@org.springframework.context.annotation.Configuration
public class MetadataObjectsConfiguration {

    private static final String metadataTableKey = "objects";

    private Map<String, MetadataObject> metadataObjectMap;
    private final Configuration configuration;

    public MetadataObjectsConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    private void postConstruct() {
        metadataObjectMap = new HashMap<>();
        if (containsConfiguration()) {
            Map<String, Object> frameworkSettingConfigurations = (Map<String, Object>) ((Map<String, Object>) configuration.getProperties()
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
        return configuration.getProperties().containsKey(MetadataConfiguration.configurationKey) &&
                (configuration.getProperties().get(MetadataConfiguration.configurationKey) instanceof Map) &&
                ((Map<String, Object>) configuration.getProperties().get(MetadataConfiguration.configurationKey)).containsKey(metadataTableKey) &&
                ((Map<String, Object>) configuration.getProperties().get(MetadataConfiguration.configurationKey)).get(metadataTableKey) instanceof Map;
    }
}
