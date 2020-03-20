package io.metadew.iesi.framework.configuration.metadata.repository.coordinator.service;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.framework.configuration.metadata.repository.coordinator.RepositoryCoordinatorDefinition;
import io.metadew.iesi.framework.configuration.metadata.repository.coordinator.RepositoryCoordinatorProfileDefinition;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

public interface MetadataRepositoryCoordinatorService<T extends RepositoryCoordinatorDefinition, V extends DatabaseConnection> {

    public RepositoryCoordinator convert(T t);
    public V getDatabaseConnection(T t, RepositoryCoordinatorProfileDefinition repositoryCoordinatorProfileDefinition);
    public Class<T> appliesTo();

}
