package io.metadew.iesi.common.configuration.metadata.repository.coordinator.mysql;

import io.metadew.iesi.SpringContext;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.IMetadataRepositoryCoordinatorService;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinatorProfileDefinition;
import io.metadew.iesi.common.crypto.FrameworkCrypto;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.mysql.MysqlDatabase;
import io.metadew.iesi.connection.database.mysql.MysqlDatabaseConnection;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;

public class MysqlMetadataRepositoryCoordinatorService implements IMetadataRepositoryCoordinatorService<MysqlMetadataRepositoryCoordinatorDefinition, MysqlDatabaseConnection> {

    private static MysqlMetadataRepositoryCoordinatorService INSTANCE;
    private final FrameworkCrypto frameworkCrypto = SpringContext.getBean(FrameworkCrypto.class);

    public synchronized static MysqlMetadataRepositoryCoordinatorService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MysqlMetadataRepositoryCoordinatorService();
        }
        return INSTANCE;
    }

    private MysqlMetadataRepositoryCoordinatorService() {
    }

    @Override
    public RepositoryCoordinator convert(MysqlMetadataRepositoryCoordinatorDefinition mysqlRepositoryCoordinatorDefinition) {
        Map<String, Database> databases = new HashMap<>();
        if (mysqlRepositoryCoordinatorDefinition.getOwner() != null) {
            MysqlDatabaseConnection databaseConnection = getDatabaseConnection(mysqlRepositoryCoordinatorDefinition,
                    mysqlRepositoryCoordinatorDefinition.getReader());
            MysqlDatabase postgresqlDatabase = new MysqlDatabase(databaseConnection);
            mysqlRepositoryCoordinatorDefinition.getSchema().ifPresent(postgresqlDatabase::setSchema);
            databases.put("owner", postgresqlDatabase);
            databases.put("writer", postgresqlDatabase);
            databases.put("reader", postgresqlDatabase);
        }
        if (mysqlRepositoryCoordinatorDefinition.getWriter() != null) {
            MysqlDatabaseConnection mssqlDatabaseConnection = getDatabaseConnection(mysqlRepositoryCoordinatorDefinition,
                    mysqlRepositoryCoordinatorDefinition.getWriter());
            MysqlDatabase mssqlDatabase = new MysqlDatabase(mssqlDatabaseConnection);
            mysqlRepositoryCoordinatorDefinition.getSchema().ifPresent(mssqlDatabase::setSchema);
            databases.put("writer", mssqlDatabase);
            databases.put("reader", mssqlDatabase);
        }
        if (mysqlRepositoryCoordinatorDefinition.getReader() != null) {
            MysqlDatabaseConnection mssqlDatabaseConnection = getDatabaseConnection(mysqlRepositoryCoordinatorDefinition,
                    mysqlRepositoryCoordinatorDefinition.getWriter());
            MysqlDatabase mssqlDatabase = new MysqlDatabase(mssqlDatabaseConnection);
            mysqlRepositoryCoordinatorDefinition.getSchema().ifPresent(mssqlDatabase::setSchema);
            databases.put("reader", mssqlDatabase);
        }

        return new RepositoryCoordinator(databases);
    }

    @Override
    public MysqlDatabaseConnection getDatabaseConnection(MysqlMetadataRepositoryCoordinatorDefinition mysqlRepositoryCoordinatorDefinition, MetadataRepositoryCoordinatorProfileDefinition metadataRepositoryCoordinatorProfileDefinition) {
        if (mysqlRepositoryCoordinatorDefinition.getConnection().isPresent()) {
            return mysqlRepositoryCoordinatorDefinition.getSchema()
                    .map(schema -> new MysqlDatabaseConnection(
                            mysqlRepositoryCoordinatorDefinition.getConnection().get(),
                            metadataRepositoryCoordinatorProfileDefinition.getUser(),
                            frameworkCrypto.decryptIfNeeded(metadataRepositoryCoordinatorProfileDefinition.getPassword()),
                            mysqlRepositoryCoordinatorDefinition.getInitSql(),
                            schema))
                    .orElseThrow(RuntimeException::new);
        } else {
            return mysqlRepositoryCoordinatorDefinition.getSchema().
                    map(s -> new MysqlDatabaseConnection(
                            mysqlRepositoryCoordinatorDefinition.getHost(),
                            mysqlRepositoryCoordinatorDefinition.getPort(),
                            s,
                            metadataRepositoryCoordinatorProfileDefinition.getUser(),
                            frameworkCrypto.decryptIfNeeded(metadataRepositoryCoordinatorProfileDefinition.getPassword()),
                            mysqlRepositoryCoordinatorDefinition.getInitSql()
                    ))
                    .orElseThrow(() -> new RuntimeException("Mysql database connection needs a schema"));
        }
    }

    @Override
    public Class<MysqlMetadataRepositoryCoordinatorDefinition> appliesTo() {
        return MysqlMetadataRepositoryCoordinatorDefinition.class;
    }
}
