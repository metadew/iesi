package io.metadew.iesi.metadata.repository;

import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

public class ExecutionServerMetadataRepository extends MetadataRepository {

    public ExecutionServerMetadataRepository(String frameworkCode, String name, String scope, String instanceName, RepositoryCoordinator repositoryCoordinator, String repositoryObjectsPath, String repositoryTablesPath) {
        super(frameworkCode, name, scope, instanceName, repositoryCoordinator, repositoryObjectsPath, repositoryTablesPath);
    }

    @Override
    public String getDefinitionFileName() {
        return "ExecutionTables.json";
    }

    @Override
    public String getObjectDefinitionFileName() {
        return "ExecutionObjects.json";
    }

    @Override
    public String getCategory() {
        return "execution_server";
    }

    @Override
    public String getCategoryPrefix() {
        return "EXE";
    }

    @Override
    public void save(DataObject dataObject) {
        System.out.println("save");
    }
}
