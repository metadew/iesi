package io.metadew.iesi.common.configuration.metadata.tables;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.metadata.definition.MetadataTable;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
public class MetadataTablesConfiguration {

    private static MetadataTablesConfiguration INSTANCE;
    private static final String metadataTableKey = "tables";

    private List<MetadataTable> metadataTables;

    public synchronized static MetadataTablesConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataTablesConfiguration();
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private MetadataTablesConfiguration() {
        metadataTables = new ArrayList<>();
        if (containsConfiguration()) {
            List<Object> frameworkSettingConfigurations =
                    (List<Object>) ((Map<String, Object>) Configuration.getInstance()
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
    @SuppressWarnings("unchecked")
    private boolean containsConfiguration() {
        return Configuration.getInstance().getProperties().containsKey(MetadataConfiguration.configurationKey) &&
                (Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey) instanceof Map)&&
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).containsKey(metadataTableKey) &&
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).get(metadataTableKey) instanceof Map;
    }

    public List<MetadataTable> getMetadataTables() {
        return metadataTables;
    }


}
