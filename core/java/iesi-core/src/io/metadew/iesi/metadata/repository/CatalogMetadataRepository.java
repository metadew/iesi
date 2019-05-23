package io.metadew.iesi.metadata.repository;

import io.metadew.iesi.framework.execution.FrameworkExecution;
import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

public class CatalogMetadataRepository extends MetadataRepository {

    public CatalogMetadataRepository(String frameworkCode, String name, String scope, String instanceName, RepositoryCoordinator repositoryCoordinator, String repositoryObjectsPath, String repositoryTablesPath) {
        super(frameworkCode, name, scope, instanceName, repositoryCoordinator, repositoryObjectsPath, repositoryTablesPath);
    }

    @Override
    public String getDefinitionFileName() {
        return null;
    }

    @Override
    public String getObjectDefinitionFileName() {
        return null;
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getCategoryPrefix() {
        return null;
    }

    @Override
    public void save(DataObject dataObject, FrameworkExecution frameworkExecution) {

    }
}
