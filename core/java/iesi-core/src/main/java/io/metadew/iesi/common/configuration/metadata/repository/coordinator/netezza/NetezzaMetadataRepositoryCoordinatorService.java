package io.metadew.iesi.common.configuration.metadata.repository.coordinator.netezza;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.IMetadataRepositoryCoordinatorService;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinatorProfileDefinition;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.netezza.NetezzaDatabase;
import io.metadew.iesi.connection.database.netezza.NetezzaDatabaseConnection;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;

public class NetezzaMetadataRepositoryCoordinatorService implements IMetadataRepositoryCoordinatorService<NetezzaMetadataRepositoryCoordinatorDefinition, NetezzaDatabaseConnection> {

    private static NetezzaMetadataRepositoryCoordinatorService INSTANCE;
    private final FrameworkCrypto frameworkCrypto = SpringContext.getBean(FrameworkCrypto.class);

    public synchronized static NetezzaMetadataRepositoryCoordinatorService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetezzaMetadataRepositoryCoordinatorService();
        }
        return INSTANCE;
    }

    private NetezzaMetadataRepositoryCoordinatorService() {
    }

    @Override
    public RepositoryCoordinator convert(NetezzaMetadataRepositoryCoordinatorDefinition mssqlRepositoryCoordinatorDefinition) {
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
                    mssqlRepositoryCoordinatorDefinition.getReader());
            NetezzaDatabase mssqlDatabase = new NetezzaDatabase(mssqlDatabaseConnection);
            mssqlRepositoryCoordinatorDefinition.getSchema().ifPresent(mssqlDatabase::setSchema);
            databases.put("reader", mssqlDatabase);
        }

        return new RepositoryCoordinator(databases);
    }

    @Override
    public NetezzaDatabaseConnection getDatabaseConnection(NetezzaMetadataRepositoryCoordinatorDefinition netezzaRepositoryCoordinatorDefinition, MetadataRepositoryCoordinatorProfileDefinition metadataRepositoryCoordinatorProfileDefinition) {
        NetezzaDatabaseConnection netezzaDatabaseConnection;
        if (netezzaRepositoryCoordinatorDefinition.getConnection().isPresent()) {
            netezzaDatabaseConnection = new NetezzaDatabaseConnection(
                    netezzaRepositoryCoordinatorDefinition.getConnection().get(),
                    metadataRepositoryCoordinatorProfileDefinition.getUser(),
                    frameworkCrypto.decryptIfNeeded(metadataRepositoryCoordinatorProfileDefinition.getPassword()),
                    netezzaRepositoryCoordinatorDefinition.getInitSql());
            netezzaRepositoryCoordinatorDefinition.getSchema().ifPresent(netezzaDatabaseConnection::setSchema);
        } else {
            netezzaDatabaseConnection = new NetezzaDatabaseConnection(
                    netezzaRepositoryCoordinatorDefinition.getHost(),
                    netezzaRepositoryCoordinatorDefinition.getPort(),
                    netezzaRepositoryCoordinatorDefinition.getDatabase(),
                    metadataRepositoryCoordinatorProfileDefinition.getUser(),
                    frameworkCrypto.decryptIfNeeded(metadataRepositoryCoordinatorProfileDefinition.getPassword()),
                    netezzaRepositoryCoordinatorDefinition.getInitSql()
            );
            netezzaRepositoryCoordinatorDefinition.getSchema().ifPresent(netezzaDatabaseConnection::setSchema);
        }
        return netezzaDatabaseConnection;
    }

    @Override
    public Class<NetezzaMetadataRepositoryCoordinatorDefinition> appliesTo() {
        return NetezzaMetadataRepositoryCoordinatorDefinition.class;
    }
}
