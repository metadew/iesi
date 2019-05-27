package io.metadew.iesi.metadata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.framework.execution.FrameworkLog;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.MetadataObject;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata.operation.DataObjectOperation;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class MetadataRepository {

    String frameworkCode;
    RepositoryCoordinator repositoryCoordinator;
    String name;
    String scope;
    String instanceName;
    private List<MetadataObject> metadataObjects;
    private List<MetadataTable> metadataTables;

    public MetadataRepository(String frameworkCode, String name, String scope, String instanceName, RepositoryCoordinator repositoryCoordinator,
                              String repositoryObjectsPath, String repositoryTablePath) {
        this.frameworkCode = frameworkCode;
        this.name = name;
        this.scope = scope;
        this.instanceName = instanceName;
        this.repositoryCoordinator = repositoryCoordinator;
        metadataObjects = new ArrayList<>();
        metadataTables = new ArrayList<>();

        DataObjectOperation dataObjectOperation = new DataObjectOperation();
        dataObjectOperation.setInputFile(repositoryObjectsPath + File.separator + getObjectDefinitionFileName());
        dataObjectOperation.parseFile();
        ObjectMapper objectMapper = new ObjectMapper();
        //
        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
            if (dataObject.getType().equalsIgnoreCase("metadataobject")) {
                metadataObjects.add(objectMapper.convertValue(dataObject.getData(), MetadataObject.class));
            }
        }

        dataObjectOperation = new DataObjectOperation();
        dataObjectOperation.setInputFile(repositoryTablePath + File.separator + getDefinitionFileName());
        dataObjectOperation.parseFile();
        //
        for (DataObject dataObject : dataObjectOperation.getDataObjects()) {
            if (dataObject.getType().equalsIgnoreCase("metadatatable")) {
                MetadataTable metadataTable = objectMapper.convertValue(dataObject.getData(), MetadataTable.class);
                // TODO: add prefix to MetadataTable definitions
                metadataTable.setName(getTableNamePrefix() + metadataTable.getName());
                metadataTables.add(metadataTable);
            }
        }

    }

    public abstract String getDefinitionFileName();

    public abstract String getObjectDefinitionFileName();

    public abstract String getCategory();

    public abstract String getCategoryPrefix();

    public String getTableNamePrefix() {
        String frameworkCodeString = getFrameworkCode().map(frameworkCode -> frameworkCode + "_").orElse("");
        String instanceNameString = getInstanceName().map(instanceName -> instanceName + "_").orElse("");
        return frameworkCodeString + instanceNameString;
    }

    public Optional<String> getFrameworkCode() {
        return Optional.ofNullable(frameworkCode);
    }

    private void dropTable(MetadataTable metadataTable) {
        repositoryCoordinator.dropTable(metadataTable);
    }

    public void dropAllTables() {
        metadataTables.forEach(this::dropTable);
    }

    private void cleanTable(MetadataTable metadataTable) {
        repositoryCoordinator.cleanTable(metadataTable);
    }

    public void cleanAllTables() {
        metadataTables.forEach(this::cleanTable);
    }

    public void dropAllTables(FrameworkLog frameworkLog) {
        metadataTables.forEach(this::dropTable);
    }

    // TODO: remove because security danger: query can target objects outside of repository responsibilities
    public CachedRowSet executeQuery(String query, String logonType) {
        return repositoryCoordinator.executeQuery(query, logonType);
    }

    // TODO: remove because security danger: query can target objects outside of repository responsibilities
    public void executeUpdate(String query) {
        repositoryCoordinator.executeUpdate(query);
    }

    // TODO: remove because security danger: query can target objects outside of repository responsibilities
    public void executeScript(String fileName, String logonType) {
        repositoryCoordinator.executeScript(fileName, logonType);
    }

    // TODO: remove because security danger: query can target objects outside of repository responsibilities
    public void executeScript(InputStream inputStream, String logonType) {
        repositoryCoordinator.executeScript(inputStream, logonType);
    }

    // TODO: remove because security danger: query can target objects outside of repository responsibilities
    public void executeScript(InputStream inputStream) {
        repositoryCoordinator.executeScript(inputStream, "writer");
    }

    private void createTable(MetadataTable metadataTable) {
        this.repositoryCoordinator.createTable(metadataTable);
    }

    public void createAllTables() {
        metadataTables.forEach(this::createTable);
    }

    public String generateDDL() {
        return metadataTables.stream()
                .map(metadataTable -> repositoryCoordinator.getCreateStatement(metadataTable))
                .collect(Collectors.joining("\n\n"));
    }

    public String getTableNameByLabel(String label) {
        return metadataTables.stream()
                .filter(metadataTable -> metadataTable.getLabel().equalsIgnoreCase(label))
                .findFirst()
                .map(MetadataTable::getName)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("No label {0} found for metadata repository {1}", label, getCategory())));
    }

    public String getName() {
        return name;
    }

    public List<MetadataTable> getMetadataTables() {
        return metadataTables;
    }

    public List<MetadataObject> getMetadataObjects() {
        return metadataObjects;
    }

    public RepositoryCoordinator getRepository() {
        return repositoryCoordinator;
    }

    private Optional<String> getInstanceName() {
        return Optional.ofNullable(instanceName);
    }

    public abstract void save(DataObject dataObject, FrameworkExecution frameworkExecution);

}
