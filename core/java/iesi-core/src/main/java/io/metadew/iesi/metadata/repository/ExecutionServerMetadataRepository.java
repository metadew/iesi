package io.metadew.iesi.metadata.repository;

import io.metadew.iesi.metadata.definition.DataObject;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

public class ExecutionServerMetadataRepository extends MetadataRepository {

    public ExecutionServerMetadataRepository(String instanceName, RepositoryCoordinator repositoryCoordinator) {
        super(instanceName, repositoryCoordinator);
    }

    @Override
    public String getCategory() {
        return "execution";
    }

    @Override
    public void save(DataObject dataObject) {}
}
