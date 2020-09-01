package io.metadew.iesi.common.configuration.metadata.repository.coordinator;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

public interface IMetadataRepositoryCoordinatorService<T extends MetadataRepositoryCoordinatorDefinition, V extends DatabaseConnection> {

    public RepositoryCoordinator convert(T t);
    public V getDatabaseConnection(T t, MetadataRepositoryCoordinatorProfileDefinition metadataRepositoryCoordinatorProfileDefinition);
    public Class<T> appliesTo();

}
