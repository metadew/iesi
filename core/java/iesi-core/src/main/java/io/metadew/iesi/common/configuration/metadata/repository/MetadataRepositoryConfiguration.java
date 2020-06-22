package io.metadew.iesi.common.configuration.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.metadata.repository.*;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
public class MetadataRepositoryConfiguration {

    private static MetadataRepositoryConfiguration INSTANCE;
    private static final String repositoryTableKey = "repository";

    private List<MetadataRepositoryDefinition> metadataRepositoryDefinitions;
    private List<MetadataRepository> metadataRepositories;

    public synchronized static MetadataRepositoryConfiguration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataRepositoryConfiguration();
        }
        return INSTANCE;
    }

    public ConnectivityMetadataRepository getConnectivityMetadataRepository() {
        return (ConnectivityMetadataRepository) metadataRepositories.stream()
                .filter(metadataRepository -> metadataRepository.getClass().isAssignableFrom(ConnectivityMetadataRepository.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ConnectivityMetadataRepository not configured"));
    }

    public TraceMetadataRepository getTraceMetadataRepository() {
        return (TraceMetadataRepository) metadataRepositories.stream()
                .filter(metadataRepository -> metadataRepository.getClass().isAssignableFrom(TraceMetadataRepository.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("TraceMetadataRepository not configured"));
    }

    public ResultMetadataRepository getResultMetadataRepository() {
        return (ResultMetadataRepository) metadataRepositories.stream()
                .filter(metadataRepository -> metadataRepository.getClass().isAssignableFrom(ResultMetadataRepository.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ResultMetadataRepository not configured"));
    }

    public DesignMetadataRepository getDesignMetadataRepository() {
        return (DesignMetadataRepository) metadataRepositories.stream()
                .filter(metadataRepository -> metadataRepository.getClass().isAssignableFrom(DesignMetadataRepository.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("DesignMetadataRepository not configured"));
    }

    public ControlMetadataRepository getControlMetadataRepository() {
        return (ControlMetadataRepository) metadataRepositories.stream()
                .filter(metadataRepository -> metadataRepository.getClass().isAssignableFrom(ControlMetadataRepository.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ControlMetadataRepository not configured"));
    }

    public ExecutionServerMetadataRepository getExecutionServerMetadataRepository() {
        return (ExecutionServerMetadataRepository) metadataRepositories.stream()
                .filter(metadataRepository -> metadataRepository.getClass().isAssignableFrom(ExecutionServerMetadataRepository.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ExecutionServerMetadataRepository not configured"));
    }

    private MetadataRepositoryConfiguration() {
        metadataRepositories = new ArrayList<>();
        metadataRepositoryDefinitions = new ArrayList<>();
        if (containsConfiguration()) {
            loadConfigurations();
            convertConfigurations();
        } else {
            log.warn("no metadata repository configurations found on system variable, classpath or filesystem");
        }
    }

    private void convertConfigurations() {
        for (MetadataRepositoryDefinition metadataRepositoryDefinition : metadataRepositoryDefinitions) {
            metadataRepositories.addAll(MetadataRepositoryService.getInstance().convert(metadataRepositoryDefinition));
        }
    }

    @SuppressWarnings("unchecked")
    private void loadConfigurations() {
        List<Object> frameworkSettingConfigurations = (List<Object>) ((Map<String, Object>) Configuration.getInstance().getProperties()
                .get(MetadataConfiguration.configurationKey))
                .get(repositoryTableKey);
        ObjectMapper objectMapper = new ObjectMapper();
        for (Object element : frameworkSettingConfigurations) {
            metadataRepositoryDefinitions.add(objectMapper.convertValue(element, MetadataRepositoryDefinition.class));
        }
    }

    @SuppressWarnings("unchecked")
    private boolean containsConfiguration() {
        return Configuration.getInstance().getProperties().containsKey(MetadataConfiguration.configurationKey) &&
                (Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey) instanceof Map) &&
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).containsKey(repositoryTableKey) &&
                ((Map<String, Object>) Configuration.getInstance().getProperties().get(MetadataConfiguration.configurationKey)).get(repositoryTableKey) instanceof List;
    }

    public List<MetadataRepository> getMetadataRepositories() {
        return metadataRepositories;
    }
}