package io.metadew.iesi.metadata_repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
import io.metadew.iesi.metadata.configuration.EnvironmentConfiguration;
import io.metadew.iesi.metadata.configuration.MetadataTableConfiguration;
import io.metadew.iesi.metadata.definition.Connection;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.definition.Environment;
import io.metadew.iesi.metadata.definition.MetadataTable;
import io.metadew.iesi.metadata_repository.repository.Repository;

import java.io.File;
import java.util.UUID;

public class ConnectivityMetadataRepository extends MetadataRepository{

    public ConnectivityMetadataRepository(String frameworkCode, String name, String scope, String instanceName, Repository repository, String repositoryObjectsPath,  String repositoryTablesPath) {
        super(frameworkCode, name, scope, instanceName, repository, repositoryObjectsPath, repositoryTablesPath);
    }

    @Override
    public String getDefinitionFileName() {
        return "ConnectivityTables.json";
    }

    @Override
    public String getObjectDefinitionFileName() {
        return "ConnectivityObjects.json";
    }

    @Override
    public String getCategory() {
        return "connectivity";
    }

    @Override
    public String getCategoryPrefix() {
        return "CXN";
    }

    @Override
    public void create(boolean generateDdl) {
        System.out.println("create");
    }

    @Override
    public void createAllTables() {
        System.out.println("create all tables");
    }

    @Override
    public void createMetadataRepository(File file, String archiveFolder, String errorFolder, UUID uuid) {
        System.out.println("create metadata repository");
    }

    @Override
    public void save(DataObject dataObject, FrameworkExecution frameworkExecution) {
        System.out.println("save");
        System.out.println(dataObject.getType());
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataObject.getType().equalsIgnoreCase("connection")) {
            Connection connection = objectMapper.convertValue(dataObject.getData(), Connection.class);
            ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(connection,
                    frameworkExecution);
            System.out.println(connectionConfiguration.getInsertStatement());
            executeQuery(connectionConfiguration.getInsertStatement(), "writer");
        } else if (dataObject.getType().equalsIgnoreCase("environment")) {
            Environment environment = objectMapper.convertValue(dataObject.getData(), Environment.class);
            EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(environment,
                    frameworkExecution);
            System.out.println(environmentConfiguration.getInsertStatement());
            executeQuery(environmentConfiguration.getInsertStatement(), "writer");
        } else 	if (dataObject.getType().equalsIgnoreCase("metadatatable")) {
			MetadataTable metadataTable = objectMapper.convertValue(dataObject.getData(), MetadataTable.class);
			this.repository.createTable(metadataTable, getTableNamePrefix());
        }
    }
}
