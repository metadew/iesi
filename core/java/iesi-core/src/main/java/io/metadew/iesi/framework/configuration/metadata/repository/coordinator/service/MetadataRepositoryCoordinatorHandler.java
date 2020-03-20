package io.metadew.iesi.framework.configuration.metadata.repository.coordinator.service;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.framework.configuration.metadata.repository.coordinator.RepositoryCoordinatorDefinition;
import io.metadew.iesi.framework.configuration.metadata.repository.coordinator.RepositoryCoordinatorProfileDefinition;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

public interface MetadataRepositoryCoordinatorHandler {

    public RepositoryCoordinator convert(RepositoryCoordinatorDefinition repositoryCoordinatorDefinition);

    public DatabaseConnection getDatabaseConnection(RepositoryCoordinatorDefinition repositoryCoordinatorDefinition, RepositoryCoordinatorProfileDefinition repositoryCoordinatorProfileDefinition);

}
