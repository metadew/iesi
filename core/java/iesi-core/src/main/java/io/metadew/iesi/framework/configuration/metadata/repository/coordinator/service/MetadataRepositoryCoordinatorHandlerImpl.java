package io.metadew.iesi.framework.configuration.metadata.repository.coordinator.service;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.framework.configuration.metadata.repository.coordinator.RepositoryCoordinatorDefinition;
import io.metadew.iesi.framework.configuration.metadata.repository.coordinator.RepositoryCoordinatorProfileDefinition;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;

public class MetadataRepositoryCoordinatorHandlerImpl implements MetadataRepositoryCoordinatorHandler {

    private static MetadataRepositoryCoordinatorHandlerImpl INSTANCE;

    private Map<Class<? extends RepositoryCoordinatorDefinition>, MetadataRepositoryCoordinatorService> metadataRepositoryCoordinatorServiceMap;


    public synchronized static MetadataRepositoryCoordinatorHandlerImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetadataRepositoryCoordinatorHandlerImpl();
        }
        return INSTANCE;
    }

    private MetadataRepositoryCoordinatorHandlerImpl() {
        metadataRepositoryCoordinatorServiceMap = new HashMap<>();
        metadataRepositoryCoordinatorServiceMap.put(H2MetadataRepositoryCoordinatorService.getInstance().appliesTo(), H2MetadataRepositoryCoordinatorService.getInstance());
        metadataRepositoryCoordinatorServiceMap.put(MssqlMetadataRepositoryCoordinatorService.getInstance().appliesTo(), MssqlMetadataRepositoryCoordinatorService.getInstance());
        metadataRepositoryCoordinatorServiceMap.put(NetezzaMetadataRepositoryCoordinatorService.getInstance().appliesTo(), NetezzaMetadataRepositoryCoordinatorService.getInstance());
        metadataRepositoryCoordinatorServiceMap.put(OracleMetadataRepositoryCoordinatorService.getInstance().appliesTo(), OracleMetadataRepositoryCoordinatorService.getInstance());
        metadataRepositoryCoordinatorServiceMap.put(PostgresqlMetadataRepositoryCoordinatorService.getInstance().appliesTo(), PostgresqlMetadataRepositoryCoordinatorService.getInstance());
        metadataRepositoryCoordinatorServiceMap.put(SqliteMetadataRepositoryCoordinatorService.getInstance().appliesTo(), SqliteMetadataRepositoryCoordinatorService.getInstance());
    }

    @Override
    @SuppressWarnings("unchecked")
    public RepositoryCoordinator convert(RepositoryCoordinatorDefinition repositoryCoordinatorDefinition) {
        return metadataRepositoryCoordinatorServiceMap.get(repositoryCoordinatorDefinition.getClass()).convert(repositoryCoordinatorDefinition);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DatabaseConnection getDatabaseConnection(RepositoryCoordinatorDefinition repositoryCoordinatorDefinition, RepositoryCoordinatorProfileDefinition repositoryCoordinatorProfileDefinition) {
        return metadataRepositoryCoordinatorServiceMap.get(repositoryCoordinatorDefinition.getClass()).getDatabaseConnection(repositoryCoordinatorDefinition, repositoryCoordinatorProfileDefinition);
    }

}
