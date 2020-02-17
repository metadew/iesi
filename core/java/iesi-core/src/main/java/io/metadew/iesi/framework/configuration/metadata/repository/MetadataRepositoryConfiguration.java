package io.metadew.iesi.framework.configuration.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.configuration.Configuration;
import io.metadew.iesi.framework.configuration.metadata.MetadataConfiguration;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
public class MetadataRepositoryConfiguration {

    private static MetadataRepositoryConfiguration INSTANCE;
    private static final String repositoryTableKey = "repository";

    private List<MetadataRepositoryDefinition> metadataRepositoryDefinitions;

    public synchronized static MetadataRepositoryConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataRepositoryConfiguration();
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private MetadataRepositoryConfiguration() {
        metadataRepositoryDefinitions = new ArrayList<>();
        if (containsConfiguration()) {
            List<Object> frameworkSettingConfigurations = (List<Object>) ((Map<String, Object>) Configuration.getInstance().getProperties()
                    .get(MetadataConfiguration.configurationKey))
                    .get(repositoryTableKey);
            ObjectMapper objectMapper = new ObjectMapper();
            for (Object element : frameworkSettingConfigurations) {
                metadataRepositoryDefinitions.add(objectMapper.convertValue(element, MetadataRepositoryDefinition.class));
            }
        } else {
            log.warn("no metadata repository configurations found on system variable, classpath or filesystem");

        }
    }

    @SuppressWarnings("unchecked")
    private boolean containsConfiguration() {
        return Configuration.getInstance().getProperties().containsKey(MetadataConfiguration.configurationKey) ||
                (Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey) instanceof Map) ||
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).containsKey(repositoryTableKey) ||
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).get(repositoryTableKey) instanceof Map;
    }
}