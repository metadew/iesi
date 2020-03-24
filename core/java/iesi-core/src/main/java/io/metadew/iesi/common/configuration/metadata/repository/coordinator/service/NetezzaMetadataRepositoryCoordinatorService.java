package io.metadew.iesi.common.configuration.metadata.repository.coordinator.service;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.NetezzaDatabase;
import io.metadew.iesi.connection.database.connection.netezza.NetezzaDatabaseConnection;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.NetezzaRepositoryCoordinatorDefinition;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.RepositoryCoordinatorProfileDefinition;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;

public class NetezzaMetadataRepositoryCoordinatorService implements MetadataRepositoryCoordinatorService<NetezzaRepositoryCoordinatorDefinition, NetezzaDatabaseConnection> {

    private static NetezzaMetadataRepositoryCoordinatorService INSTANCE;

    public synchronized static NetezzaMetadataRepositoryCoordinatorService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetezzaMetadataRepositoryCoordinatorService();
        }
        return INSTANCE;
    }

    private NetezzaMetadataRepositoryCoordinatorService() {
    }

    @Override
    public RepositoryCoordinator convert(NetezzaRepositoryCoordinatorDefinition mssqlRepositoryCoordinatorDefinition) {
        Map<String, Database> databases = new HashMap<>();
        if (mssqlRepositoryCoordinatorDefinition.getOwner() != null) {
            NetezzaDatabaseConnection mssqlDatabaseConnection = getDatabaseConnection(mssqlRepositoryCoordinatorDefinition,
                    mssqlRepositoryCoordinatorDefinition.getOwner());
            NetezzaDatabase mssqlDatabase = new NetezzaDatabase(mssqlDatabaseConnection);
            mssqlRepositoryCoordinatorDefinition.getSchema().ifPresent(mssqlDatabase::setSchema);
            databases.put("owner", mssqlDatabase);
            databases.put("writer", mssqlDatabase);
            databases.put("reader", mssqlDatabase);
        }
        if (mssqlRepositoryCoordinatorDefinition.getWriter() != null) {
            NetezzaDatabaseConnection mssqlDatabaseConnection = getDatabaseConnection(mssqlRepositoryCoordinatorDefinition,
                    mssqlRepositoryCoordinatorDefinition.getWriter());
            NetezzaDatabase mssqlDatabase = new NetezzaDatabase(mssqlDatabaseConnection);
            mssqlRepositoryCoordinatorDefinition.getSchema().ifPresent(mssqlDatabase::setSchema);
            databases.put("writer", mssqlDatabase);
            databases.put("reader", mssqlDatabase);
        }
        if (mssqlRepositoryCoordinatorDefinition.getReader() != null) {
            NetezzaDatabaseConnection mssqlDatabaseConnection = getDatabaseConnection(mssqlRepositoryCoordinatorDefinition,
                    mssqlRepositoryCoordinatorDefinition.getWriter());
            NetezzaDatabase mssqlDatabase = new NetezzaDatabase(mssqlDatabaseConnection);
            mssqlRepositoryCoordinatorDefinition.getSchema().ifPresent(mssqlDatabase::setSchema);
            databases.put("reader", mssqlDatabase);
        }

        return new RepositoryCoordinator(databases);
    }

    @Override
    public NetezzaDatabaseConnection getDatabaseConnection(NetezzaRepositoryCoordinatorDefinition netezzaRepositoryCoordinatorDefinition, RepositoryCoordinatorProfileDefinition repositoryCoordinatorProfileDefinition) {
        if (netezzaRepositoryCoordinatorDefinition.getConnection().isPresent()) {
            return new NetezzaDatabaseConnection(
                    netezzaRepositoryCoordinatorDefinition.getConnection().get(),
                    repositoryCoordinatorProfileDefinition.getUser(),
                    FrameworkCrypto.getInstance().decryptIfNeeded(repositoryCoordinatorProfileDefinition.getPassword()));
        } else {
            return new NetezzaDatabaseConnection(
                    netezzaRepositoryCoordinatorDefinition.getHost(),
                    netezzaRepositoryCoordinatorDefinition.getPort(),
                    netezzaRepositoryCoordinatorDefinition.getDatabase(),
                    repositoryCoordinatorProfileDefinition.getUser(),
                    FrameworkCrypto.getInstance().decryptIfNeeded(repositoryCoordinatorProfileDefinition.getPassword())
            );
        }
    }

    @Override
    public Class<NetezzaRepositoryCoordinatorDefinition> appliesTo() {
        return NetezzaRepositoryCoordinatorDefinition.class;
    }
}
