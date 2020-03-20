package io.metadew.iesi.framework.configuration.metadata.repository.coordinator.service;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.MssqlDatabase;
import io.metadew.iesi.connection.database.connection.mssql.MssqlDatabaseConnection;
import io.metadew.iesi.framework.configuration.metadata.repository.coordinator.MssqlRepositoryCoordinatorDefinition;
import io.metadew.iesi.framework.configuration.metadata.repository.coordinator.RepositoryCoordinatorProfileDefinition;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;

public class MssqlMetadataRepositoryCoordinatorService implements MetadataRepositoryCoordinatorService<MssqlRepositoryCoordinatorDefinition, MssqlDatabaseConnection> {

    private static MssqlMetadataRepositoryCoordinatorService INSTANCE;

    public synchronized static MssqlMetadataRepositoryCoordinatorService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MssqlMetadataRepositoryCoordinatorService();
        }
        return INSTANCE;
    }

    private MssqlMetadataRepositoryCoordinatorService() {
    }

    @Override
    public RepositoryCoordinator convert(MssqlRepositoryCoordinatorDefinition mssqlRepositoryCoordinatorDefinition) {
        Map<String, Database> databases = new HashMap<>();
        if (mssqlRepositoryCoordinatorDefinition.getOwner() != null) {
            MssqlDatabaseConnection mssqlDatabaseConnection = getDatabaseConnection(mssqlRepositoryCoordinatorDefinition,
                    mssqlRepositoryCoordinatorDefinition.getOwner());
            MssqlDatabase mssqlDatabase = new MssqlDatabase(mssqlDatabaseConnection);
            mssqlRepositoryCoordinatorDefinition.getSchema().ifPresent(mssqlDatabase::setSchema);
            databases.put("owner", mssqlDatabase);
            databases.put("writer", mssqlDatabase);
            databases.put("reader", mssqlDatabase);
        }
        if (mssqlRepositoryCoordinatorDefinition.getWriter() != null) {
            MssqlDatabaseConnection mssqlDatabaseConnection = getDatabaseConnection(mssqlRepositoryCoordinatorDefinition,
                    mssqlRepositoryCoordinatorDefinition.getWriter());
            MssqlDatabase mssqlDatabase = new MssqlDatabase(mssqlDatabaseConnection);
            mssqlRepositoryCoordinatorDefinition.getSchema().ifPresent(mssqlDatabase::setSchema);
            databases.put("writer", mssqlDatabase);
            databases.put("reader", mssqlDatabase);
        }
        if (mssqlRepositoryCoordinatorDefinition.getReader() != null) {
            MssqlDatabaseConnection mssqlDatabaseConnection = getDatabaseConnection(mssqlRepositoryCoordinatorDefinition,
                    mssqlRepositoryCoordinatorDefinition.getWriter());
            MssqlDatabase mssqlDatabase = new MssqlDatabase(mssqlDatabaseConnection);
            mssqlRepositoryCoordinatorDefinition.getSchema().ifPresent(mssqlDatabase::setSchema);
            databases.put("reader", mssqlDatabase);
        }

        return new RepositoryCoordinator(databases);
    }

    @Override
    public MssqlDatabaseConnection getDatabaseConnection(MssqlRepositoryCoordinatorDefinition mssqlRepositoryCoordinatorDefinition, RepositoryCoordinatorProfileDefinition repositoryCoordinatorProfileDefinition) {
        if (mssqlRepositoryCoordinatorDefinition.getConnection().isPresent()) {
            return new MssqlDatabaseConnection(
                    mssqlRepositoryCoordinatorDefinition.getConnection().get(),
                    repositoryCoordinatorProfileDefinition.getUser(),
                    FrameworkCrypto.getInstance().decryptIfNeeded(repositoryCoordinatorProfileDefinition.getPassword()));
        } else {
            return new MssqlDatabaseConnection(
                    mssqlRepositoryCoordinatorDefinition.getHost(),
                    mssqlRepositoryCoordinatorDefinition.getPort(),
                    mssqlRepositoryCoordinatorDefinition.getDatabase(),
                    repositoryCoordinatorProfileDefinition.getUser(),
                    FrameworkCrypto.getInstance().decryptIfNeeded(repositoryCoordinatorProfileDefinition.getPassword())
            );
        }
    }

    @Override
    public Class<MssqlRepositoryCoordinatorDefinition> appliesTo() {
        return MssqlRepositoryCoordinatorDefinition.class;
    }
}
