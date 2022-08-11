package io.metadew.iesi.common.configuration.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.common.configuration.Configuration;
import io.metadew.iesi.common.configuration.metadata.MetadataConfiguration;
import io.metadew.iesi.metadata.repository.*;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@org.springframework.context.annotation.Configuration
@DependsOn({ "metadataTablesConfiguration", "metadataObjectsConfiguration" })
public class MetadataRepositoryConfiguration {

    private static final String repositoryTableKey = "repository";

    private List<MetadataRepositoryDefinition> metadataRepositoryDefinitions;
    @Getter
    private List<MetadataRepository> metadataRepositories;

    private final Configuration configuration;
    private final MetadataRepositoryService metadataRepositoryService;

    public MetadataRepositoryConfiguration(Configuration configuration, MetadataRepositoryService metadataRepositoryService) {
        this.configuration = configuration;
        this.metadataRepositoryService = metadataRepositoryService;
    }


    @PostConstruct
    private void postConstruct() {
        // init the MetadataTables and MetadataObjects configuration before creating the metadata repositories
        metadataRepositories = new ArrayList<>();
        metadataRepositoryDefinitions = new ArrayList<>();
        if (containsConfiguration()) {
            loadConfigurations();
            convertConfigurations();
        } else {
            log.warn("no metadata repository configurations found on system variable, classpath or filesystem");
        }
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

    public DataMetadataRepository getDataMetadataRepository() {
        return (DataMetadataRepository) metadataRepositories.stream()
                .filter(metadataRepository -> metadataRepository.getClass().isAssignableFrom(DataMetadataRepository.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("DataMetadataRepository not configured"));
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

    private void convertConfigurations() {
        System.out.println("AFTER CONVERT: " + metadataRepositoryDefinitions);
        for (MetadataRepositoryDefinition metadataRepositoryDefinition : metadataRepositoryDefinitions) {
            metadataRepositories.addAll(metadataRepositoryService.convert(metadataRepositoryDefinition));
        }
    }

    @SuppressWarnings("unchecked")
    private void loadConfigurations() {
        List<Object> frameworkSettingConfigurations = (List<Object>) ((Map<String, Object>) configuration.getProperties()
                .get(MetadataConfiguration.configurationKey))
                .get(repositoryTableKey);
        ObjectMapper objectMapper = new ObjectMapper();
        for (Object element : frameworkSettingConfigurations) {
            metadataRepositoryDefinitions.add(objectMapper.convertValue(element, MetadataRepositoryDefinition.class));
        }
    }

    @SuppressWarnings("unchecked")
    private boolean containsConfiguration() {
        return configuration.getProperties().containsKey(MetadataConfiguration.configurationKey) &&
                (configuration.getProperties().get(MetadataConfiguration.configurationKey) instanceof Map) &&
                ((Map<String, Object>) configuration.getProperties().get(MetadataConfiguration.configurationKey)).containsKey(repositoryTableKey) &&
                ((Map<String, Object>) configuration.getProperties().get(MetadataConfiguration.configurationKey)).get(repositoryTableKey) instanceof List;
    }

    public void createAllTables() {
        getMetadataRepositories()
                .forEach(MetadataRepository::createAllTables);
    }

    public void clearAllTables() {
        getMetadataRepositories()
                .forEach(MetadataRepository::cleanAllTables);
    }

    public void dropAllTables() {
        getMetadataRepositories()
                .forEach(MetadataRepository::dropAllTables);
    }

    public List<MetadataRepository> getMetadataRepositories() {
        return metadataRepositories;
    }
}