package io.metadew.iesi.common.configuration.metadata.repository.coordinator.service;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.OracleDatabase;
import io.metadew.iesi.connection.database.connection.oracle.OracleDatabaseConnection;
import io.metadew.iesi.connection.database.connection.oracle.ServiceNameOracleDatabaseConnection;
import io.metadew.iesi.connection.database.connection.oracle.TnsAliasOracleDatabaseConnection;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.OracleRepositoryCoordinatorDefinition;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.RepositoryCoordinatorProfileDefinition;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;

public class OracleMetadataRepositoryCoordinatorService implements MetadataRepositoryCoordinatorService<OracleRepositoryCoordinatorDefinition, OracleDatabaseConnection> {

    private static OracleMetadataRepositoryCoordinatorService INSTANCE;

    public synchronized static OracleMetadataRepositoryCoordinatorService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OracleMetadataRepositoryCoordinatorService();
        }
        return INSTANCE;
    }

    private OracleMetadataRepositoryCoordinatorService() {
    }

    @Override
    public RepositoryCoordinator convert(OracleRepositoryCoordinatorDefinition oracleRepositoryCoordinatorDefinition) {
        Map<String, Database> databases = new HashMap<>();
        if (oracleRepositoryCoordinatorDefinition.getOwner() != null) {
            OracleDatabaseConnection databaseConnection = getDatabaseConnection(oracleRepositoryCoordinatorDefinition,
                    oracleRepositoryCoordinatorDefinition.getOwner());
            OracleDatabase oracleDatabase = new OracleDatabase(databaseConnection);
            oracleRepositoryCoordinatorDefinition.getSchema().ifPresent(oracleDatabase::setSchema);
            databases.put("owner", oracleDatabase);
            databases.put("writer", oracleDatabase);
            databases.put("reader", oracleDatabase);
        }
        if (oracleRepositoryCoordinatorDefinition.getWriter() != null) {
            OracleDatabaseConnection h2DatabaseConnection = getDatabaseConnection(oracleRepositoryCoordinatorDefinition,
                    oracleRepositoryCoordinatorDefinition.getWriter());
            OracleDatabase h2Database = new OracleDatabase(h2DatabaseConnection);
            oracleRepositoryCoordinatorDefinition.getSchema().ifPresent(h2Database::setSchema);
            databases.put("writer", h2Database);
            databases.put("reader", h2Database);
        }
        if (oracleRepositoryCoordinatorDefinition.getReader() != null) {
            OracleDatabaseConnection h2DatabaseConnection = getDatabaseConnection(oracleRepositoryCoordinatorDefinition,
                    oracleRepositoryCoordinatorDefinition.getWriter());
            OracleDatabase h2Database = new OracleDatabase(h2DatabaseConnection);
            oracleRepositoryCoordinatorDefinition.getSchema().ifPresent(h2Database::setSchema);
            databases.put("reader", h2Database);
        }

        return new RepositoryCoordinator(databases);
    }

    @Override
    public Class<OracleRepositoryCoordinatorDefinition> appliesTo() {
        return OracleRepositoryCoordinatorDefinition.class;
    }

    public OracleDatabaseConnection getDatabaseConnection(OracleRepositoryCoordinatorDefinition oracleRepositoryCoordinatorDefinition,
                                                          RepositoryCoordinatorProfileDefinition repositoryCoordinatorProfileDefinition) {
        if (oracleRepositoryCoordinatorDefinition.getConnection().isPresent()) {
            return new OracleDatabaseConnection(
                    oracleRepositoryCoordinatorDefinition.getConnection().get(),
                    repositoryCoordinatorProfileDefinition.getUser(),
                    FrameworkCrypto.getInstance().decryptIfNeeded(repositoryCoordinatorProfileDefinition.getPassword()));
        } else {
            OracleDatabaseConnection databaseConnection;
            switch (oracleRepositoryCoordinatorDefinition.getMode()) {
                case "tns":
                    databaseConnection = new TnsAliasOracleDatabaseConnection(
                            oracleRepositoryCoordinatorDefinition.getHost(),
                            oracleRepositoryCoordinatorDefinition.getPort(),
                            oracleRepositoryCoordinatorDefinition.getTnsAlias(),
                            repositoryCoordinatorProfileDefinition.getUser(),
                            FrameworkCrypto.getInstance().decryptIfNeeded(repositoryCoordinatorProfileDefinition.getPassword()));
                    oracleRepositoryCoordinatorDefinition.getSchema().ifPresent(databaseConnection::setSchema);
                    break;
                case "service":
                    databaseConnection = new ServiceNameOracleDatabaseConnection(
                            oracleRepositoryCoordinatorDefinition.getHost(),
                            oracleRepositoryCoordinatorDefinition.getPort(),
                            oracleRepositoryCoordinatorDefinition.getService(),
                            repositoryCoordinatorProfileDefinition.getUser(),
                            FrameworkCrypto.getInstance().decryptIfNeeded(repositoryCoordinatorProfileDefinition.getPassword()));
                    oracleRepositoryCoordinatorDefinition.getSchema().ifPresent(databaseConnection::setSchema);
                    break;
                default:
                    throw new RuntimeException();
            }
            return databaseConnection;
        }
    }

}
