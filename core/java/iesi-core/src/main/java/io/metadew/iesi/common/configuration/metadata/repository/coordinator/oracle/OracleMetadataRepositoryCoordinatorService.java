package io.metadew.iesi.common.configuration.metadata.repository.coordinator.oracle;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.IMetadataRepositoryCoordinatorService;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinatorProfileDefinition;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.oracle.OracleDatabase;
import io.metadew.iesi.connection.database.oracle.OracleDatabaseConnection;
import io.metadew.iesi.connection.database.oracle.ServiceNameOracleDatabaseConnection;
import io.metadew.iesi.connection.database.oracle.TnsAliasOracleDatabaseConnection;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;

public class OracleMetadataRepositoryCoordinatorService implements IMetadataRepositoryCoordinatorService<OracleMetadataRepositoryCoordinatorDefinition, OracleDatabaseConnection> {

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
    public RepositoryCoordinator convert(OracleMetadataRepositoryCoordinatorDefinition oracleRepositoryCoordinatorDefinition) {
        Map<String, Database> databases = new HashMap<>();
        if (oracleRepositoryCoordinatorDefinition.getOwner() != null) {
            OracleDatabaseConnection databaseConnection = getDatabaseConnection(oracleRepositoryCoordinatorDefinition,
                    oracleRepositoryCoordinatorDefinition.getOwner());
            OracleDatabase oracleDatabase = new OracleDatabase(databaseConnection);
            oracleRepositoryCoordinatorDefinition.getSchema().ifPresent(oracleDatabase::setSchema);
            databases.put("owner", oracleDatabase);
            databases.put("writer", oracleDatabase);
            databases.put("reader", oracleDatabase);
        } else if (oracleRepositoryCoordinatorDefinition.getWriter() != null) {
            OracleDatabaseConnection h2DatabaseConnection = getDatabaseConnection(oracleRepositoryCoordinatorDefinition,
                    oracleRepositoryCoordinatorDefinition.getWriter());
            OracleDatabase h2Database = new OracleDatabase(h2DatabaseConnection);
            oracleRepositoryCoordinatorDefinition.getSchema().ifPresent(h2Database::setSchema);
            databases.put("writer", h2Database);
            databases.put("reader", h2Database);
        } else if (oracleRepositoryCoordinatorDefinition.getReader() != null) {
            OracleDatabaseConnection h2DatabaseConnection = getDatabaseConnection(oracleRepositoryCoordinatorDefinition,
                    oracleRepositoryCoordinatorDefinition.getReader());
            OracleDatabase h2Database = new OracleDatabase(h2DatabaseConnection);
            oracleRepositoryCoordinatorDefinition.getSchema().ifPresent(h2Database::setSchema);
            databases.put("reader", h2Database);
        }

        return new RepositoryCoordinator(databases);
    }

    @Override
    public Class<OracleMetadataRepositoryCoordinatorDefinition> appliesTo() {
        return OracleMetadataRepositoryCoordinatorDefinition.class;
    }

    public OracleDatabaseConnection getDatabaseConnection(OracleMetadataRepositoryCoordinatorDefinition oracleRepositoryCoordinatorDefinition,
                                                          MetadataRepositoryCoordinatorProfileDefinition metadataRepositoryCoordinatorProfileDefinition) {
        OracleDatabaseConnection databaseConnection;
        if (oracleRepositoryCoordinatorDefinition.getConnection().isPresent()) {
            databaseConnection = new OracleDatabaseConnection(
                    oracleRepositoryCoordinatorDefinition.getConnection().get(),
                    metadataRepositoryCoordinatorProfileDefinition.getUser(),
                    SpringContext.getBean(FrameworkCrypto.class).decryptIfNeeded(metadataRepositoryCoordinatorProfileDefinition.getPassword()),
                    oracleRepositoryCoordinatorDefinition.getInitSql());
            oracleRepositoryCoordinatorDefinition.getSchema().ifPresent(databaseConnection::setSchema);
        } else {
            switch (oracleRepositoryCoordinatorDefinition.getMode()) {
                case "tns":
                    databaseConnection = new TnsAliasOracleDatabaseConnection(
                            oracleRepositoryCoordinatorDefinition.getHost(),
                            oracleRepositoryCoordinatorDefinition.getPort(),
                            oracleRepositoryCoordinatorDefinition.getTnsAlias(),
                            metadataRepositoryCoordinatorProfileDefinition.getUser(),
                            SpringContext.getBean(FrameworkCrypto.class).decryptIfNeeded(metadataRepositoryCoordinatorProfileDefinition.getPassword()),
                            oracleRepositoryCoordinatorDefinition.getInitSql());
                    oracleRepositoryCoordinatorDefinition.getSchema().ifPresent(databaseConnection::setSchema);
                    break;
                case "service":
                    databaseConnection = new ServiceNameOracleDatabaseConnection(
                            oracleRepositoryCoordinatorDefinition.getHost(),
                            oracleRepositoryCoordinatorDefinition.getPort(),
                            oracleRepositoryCoordinatorDefinition.getService(),
                            metadataRepositoryCoordinatorProfileDefinition.getUser(),
                            SpringContext.getBean(FrameworkCrypto.class).decryptIfNeeded(metadataRepositoryCoordinatorProfileDefinition.getPassword()),
                            oracleRepositoryCoordinatorDefinition.getInitSql());
                    oracleRepositoryCoordinatorDefinition.getSchema().ifPresent(databaseConnection::setSchema);
                    break;
                default:
                    throw new RuntimeException();
            }
        }
        return databaseConnection;
    }

}
