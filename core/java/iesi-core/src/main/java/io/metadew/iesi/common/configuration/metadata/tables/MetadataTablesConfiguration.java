package io.metadew.iesi.common.configuration.metadata.tables;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.metadata.definition.MetadataTable;
import lombok.extern.log4j.Log4j2;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@org.springframework.context.annotation.Configuration
public class MetadataTablesConfiguration {

    private static final String metadataTableKey = "tables";

    private List<MetadataTable> metadataTables;
    private final Configuration configuration;

    public MetadataTablesConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    private void postConstruct() {
        metadataTables = new ArrayList<>();
        if (containsConfiguration()) {
            List<Object> frameworkSettingConfigurations =
                    (List<Object>) ((Map<String, Object>) configuration
                            .getProperties()
                            .get(MetadataConfiguration.configurationKey))
                            .get(metadataTableKey);
            ObjectMapper objectMapper = new ObjectMapper();
            for (Object entry : frameworkSettingConfigurations) {
                metadataTables.add(objectMapper.convertValue(entry, MetadataTable.class));
            }
        } else {
            log.warn("no metadata table configurations found on system variable, classpath or filesystem");

        }
    }

    public MetadataTable getMetadataTableNameByLabel(String label) {
        return metadataTables.stream()
                .filter(entry -> entry.getLabel().equalsIgnoreCase(label))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find MetadataTable with label: " + label));
    }

    @SuppressWarnings("unchecked")
    private boolean containsConfiguration() {
        return configuration.getProperties().containsKey(MetadataConfiguration.configurationKey) &&
                (configuration.getProperties().get(MetadataConfiguration.configurationKey) instanceof Map) &&
                ((Map<String, Object>) configuration.getProperties().get(MetadataConfiguration.configurationKey)).containsKey(metadataTableKey) &&
                ((Map<String, Object>) configuration.getProperties().get(MetadataConfiguration.configurationKey)).get(metadataTableKey) instanceof List;
    }

    public List<MetadataTable> getMetadataTables() {
        return metadataTables;
    }


}
