package io.metadew.iesi.metadata.repository;

import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

public class MonitorMetadataRepository extends MetadataRepository {

    public MonitorMetadataRepository(String name, String scope, String instanceName, RepositoryCoordinator repositoryCoordinator) {
        super(name, scope, instanceName, repositoryCoordinator);
    }

    public MonitorMetadataRepository(String name, String instanceName, RepositoryCoordinator repositoryCoordinator) {
        super(name, instanceName, repositoryCoordinator);
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
    public void save(DataObject dataObject) {

    }
}
