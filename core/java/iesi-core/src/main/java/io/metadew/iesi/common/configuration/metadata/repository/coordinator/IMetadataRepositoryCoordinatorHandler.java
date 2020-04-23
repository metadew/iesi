package io.metadew.iesi.common.configuration.metadata.repository.coordinator;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

public interface IMetadataRepositoryCoordinatorHandler {

    public RepositoryCoordinator convert(MetadataRepositoryCoordinatorDefinition metadataRepositoryCoordinatorDefinition);

    public DatabaseConnection getDatabaseConnection(MetadataRepositoryCoordinatorDefinition metadataRepositoryCoordinatorDefinition, MetadataRepositoryCoordinatorProfileDefinition metadataRepositoryCoordinatorProfileDefinition);

}
