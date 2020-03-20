package io.metadew.iesi.framework.configuration.metadata.repository.coordinator.service;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.PostgresqlDatabase;
import io.metadew.iesi.connection.database.connection.postgresql.PostgresqlDatabaseConnection;
import io.metadew.iesi.framework.configuration.metadata.repository.coordinator.PostgresqlRepositoryCoordinatorDefinition;
import io.metadew.iesi.framework.configuration.metadata.repository.coordinator.RepositoryCoordinatorProfileDefinition;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;

public class PostgresqlMetadataRepositoryCoordinatorService implements MetadataRepositoryCoordinatorService<PostgresqlRepositoryCoordinatorDefinition, PostgresqlDatabaseConnection> {

    private static PostgresqlMetadataRepositoryCoordinatorService INSTANCE;

    public synchronized static PostgresqlMetadataRepositoryCoordinatorService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PostgresqlMetadataRepositoryCoordinatorService();
        }
        return INSTANCE;
    }

    private PostgresqlMetadataRepositoryCoordinatorService() {
    }

    @Override
    public RepositoryCoordinator convert(PostgresqlRepositoryCoordinatorDefinition postgresqlRepositoryCoordinatorDefinition) {
        Map<String, Database> databases = new HashMap<>();
        if (postgresqlRepositoryCoordinatorDefinition.getOwner() != null) {
            PostgresqlDatabaseConnection databaseConnection = getDatabaseConnection(postgresqlRepositoryCoordinatorDefinition,
                    postgresqlRepositoryCoordinatorDefinition.getOwner());
            PostgresqlDatabase postgresqlDatabase = new PostgresqlDatabase(databaseConnection);
            postgresqlRepositoryCoordinatorDefinition.getSchema().ifPresent(postgresqlDatabase::setSchema);
            databases.put("owner", postgresqlDatabase);
            databases.put("writer", postgresqlDatabase);
            databases.put("reader", postgresqlDatabase);
        }
        if (postgresqlRepositoryCoordinatorDefinition.getWriter() != null) {
            PostgresqlDatabaseConnection mssqlDatabaseConnection = getDatabaseConnection(postgresqlRepositoryCoordinatorDefinition,
                    postgresqlRepositoryCoordinatorDefinition.getWriter());
            PostgresqlDatabase mssqlDatabase = new PostgresqlDatabase(mssqlDatabaseConnection);
            postgresqlRepositoryCoordinatorDefinition.getSchema().ifPresent(mssqlDatabase::setSchema);
            databases.put("writer", mssqlDatabase);
            databases.put("reader", mssqlDatabase);
        }
        if (postgresqlRepositoryCoordinatorDefinition.getReader() != null) {
            PostgresqlDatabaseConnection mssqlDatabaseConnection = getDatabaseConnection(postgresqlRepositoryCoordinatorDefinition,
                    postgresqlRepositoryCoordinatorDefinition.getWriter());
            PostgresqlDatabase mssqlDatabase = new PostgresqlDatabase(mssqlDatabaseConnection);
            postgresqlRepositoryCoordinatorDefinition.getSchema().ifPresent(mssqlDatabase::setSchema);
            databases.put("reader", mssqlDatabase);
        }

        return new RepositoryCoordinator(databases);
    }

    @Override
    public PostgresqlDatabaseConnection getDatabaseConnection(PostgresqlRepositoryCoordinatorDefinition postgresqlRepositoryCoordinatorDefinition, RepositoryCoordinatorProfileDefinition repositoryCoordinatorProfileDefinition) {
        if (postgresqlRepositoryCoordinatorDefinition.getConnection().isPresent()) {
            return new PostgresqlDatabaseConnection(
                    postgresqlRepositoryCoordinatorDefinition.getConnection().get(),
                    repositoryCoordinatorProfileDefinition.getUser(),
                    FrameworkCrypto.getInstance().decryptIfNeeded(repositoryCoordinatorProfileDefinition.getPassword()));
        } else {
            return new PostgresqlDatabaseConnection(
                    postgresqlRepositoryCoordinatorDefinition.getHost(),
                    postgresqlRepositoryCoordinatorDefinition.getPort(),
                    postgresqlRepositoryCoordinatorDefinition.getDatabase(),
                    repositoryCoordinatorProfileDefinition.getUser(),
                    FrameworkCrypto.getInstance().decryptIfNeeded(repositoryCoordinatorProfileDefinition.getPassword())
            );
        }
    }

    @Override
    public Class<PostgresqlRepositoryCoordinatorDefinition> appliesTo() {
        return PostgresqlRepositoryCoordinatorDefinition.class;
    }
}
