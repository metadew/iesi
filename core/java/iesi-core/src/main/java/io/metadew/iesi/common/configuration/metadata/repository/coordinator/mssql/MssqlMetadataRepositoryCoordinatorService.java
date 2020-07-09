package io.metadew.iesi.common.configuration.metadata.repository.coordinator.mssql;

import io.metadew.iesi.common.configuration.metadata.repository.coordinator.IMetadataRepositoryCoordinatorService;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinatorProfileDefinition;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.mssql.MssqlDatabase;
import io.metadew.iesi.connection.database.mssql.MssqlDatabaseConnection;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;

public class MssqlMetadataRepositoryCoordinatorService implements IMetadataRepositoryCoordinatorService<MssqlMetadataRepositoryCoordinatorDefinition, MssqlDatabaseConnection> {

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
    public RepositoryCoordinator convert(MssqlMetadataRepositoryCoordinatorDefinition mssqlRepositoryCoordinatorDefinition) throws Exception {
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
                    mssqlRepositoryCoordinatorDefinition.getReader());
            MssqlDatabase mssqlDatabase = new MssqlDatabase(mssqlDatabaseConnection);
            mssqlRepositoryCoordinatorDefinition.getSchema().ifPresent(mssqlDatabase::setSchema);
            databases.put("reader", mssqlDatabase);
        }

        return new RepositoryCoordinator(databases);
    }

    @Override
    public MssqlDatabaseConnection getDatabaseConnection(MssqlMetadataRepositoryCoordinatorDefinition mssqlRepositoryCoordinatorDefinition, MetadataRepositoryCoordinatorProfileDefinition metadataRepositoryCoordinatorProfileDefinition) throws Exception {
        if (mssqlRepositoryCoordinatorDefinition.getConnection().isPresent()) {
            return new MssqlDatabaseConnection(
                    mssqlRepositoryCoordinatorDefinition.getConnection().get(),
                    metadataRepositoryCoordinatorProfileDefinition.getUser(),
                    FrameworkCrypto.getInstance().decryptIfNeeded(metadataRepositoryCoordinatorProfileDefinition.getPassword()),
                    mssqlRepositoryCoordinatorDefinition.getInitSql(),
                    mssqlRepositoryCoordinatorDefinition.getSchema().orElse(null));
        } else {
            return new MssqlDatabaseConnection(
                    mssqlRepositoryCoordinatorDefinition.getHost(),
                    mssqlRepositoryCoordinatorDefinition.getPort(),
                    mssqlRepositoryCoordinatorDefinition.getDatabase(),
                    metadataRepositoryCoordinatorProfileDefinition.getUser(),
                    FrameworkCrypto.getInstance().decryptIfNeeded(metadataRepositoryCoordinatorProfileDefinition.getPassword()),
                    mssqlRepositoryCoordinatorDefinition.getInitSql(),
                    mssqlRepositoryCoordinatorDefinition.getSchema().orElse(null)
            );
        }
    }

    @Override
    public Class<MssqlMetadataRepositoryCoordinatorDefinition> appliesTo() {
        return MssqlMetadataRepositoryCoordinatorDefinition.class;
    }
}
