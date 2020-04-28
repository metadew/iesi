package io.metadew.iesi.common.configuration.metadata.repository.coordinator.postgresql;

import io.metadew.iesi.common.configuration.metadata.repository.coordinator.IMetadataRepositoryCoordinatorService;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.postgresql.PostgresqlDatabase;
import io.metadew.iesi.connection.database.postgresql.PostgresqlDatabaseConnection;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinatorProfileDefinition;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;

public class PostgresqlMetadataRepositoryCoordinatorService implements IMetadataRepositoryCoordinatorService<PostgresqlMetadataRepositoryCoordinatorDefinition, PostgresqlDatabaseConnection> {

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
    public RepositoryCoordinator convert(PostgresqlMetadataRepositoryCoordinatorDefinition postgresqlRepositoryCoordinatorDefinition) {
        Map<String, Database> databases = new HashMap<>();
        if (postgresqlRepositoryCoordinatorDefinition.getOwner() != null) {
            PostgresqlDatabaseConnection databaseConnection = getDatabaseConnection(postgresqlRepositoryCoordinatorDefinition,
                    postgresqlRepositoryCoordinatorDefinition.getReader());
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
    public PostgresqlDatabaseConnection getDatabaseConnection(PostgresqlMetadataRepositoryCoordinatorDefinition postgresqlRepositoryCoordinatorDefinition, MetadataRepositoryCoordinatorProfileDefinition metadataRepositoryCoordinatorProfileDefinition) {
        if (postgresqlRepositoryCoordinatorDefinition.getConnection().isPresent()) {
            return new PostgresqlDatabaseConnection(
                    postgresqlRepositoryCoordinatorDefinition.getConnection().get(),
                    metadataRepositoryCoordinatorProfileDefinition.getUser(),
                    FrameworkCrypto.getInstance().decryptIfNeeded(metadataRepositoryCoordinatorProfileDefinition.getPassword()));
        } else {
            return new PostgresqlDatabaseConnection(
                    postgresqlRepositoryCoordinatorDefinition.getHost(),
                    postgresqlRepositoryCoordinatorDefinition.getPort(),
                    postgresqlRepositoryCoordinatorDefinition.getDatabase(),
                    metadataRepositoryCoordinatorProfileDefinition.getUser(),
                    FrameworkCrypto.getInstance().decryptIfNeeded(metadataRepositoryCoordinatorProfileDefinition.getPassword())
            );
        }
    }

    @Override
    public Class<PostgresqlMetadataRepositoryCoordinatorDefinition> appliesTo() {
        return PostgresqlMetadataRepositoryCoordinatorDefinition.class;
    }
}
