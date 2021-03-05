package io.metadew.iesi.common.configuration.metadata.repository.coordinator.sqlite;

import io.metadew.iesi.common.FrameworkControl;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.IMetadataRepositoryCoordinatorService;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.MetadataRepositoryCoordinatorProfileDefinition;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.sqlite.SqliteDatabase;
import io.metadew.iesi.connection.database.sqlite.SqliteDatabaseConnection;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;

public class SqliteMetadataRepositoryCoordinatorService implements IMetadataRepositoryCoordinatorService<SQLiteMetadataRepositoryCoordinatorDefinition, SqliteDatabaseConnection> {

    private static SqliteMetadataRepositoryCoordinatorService INSTANCE;

    public synchronized static SqliteMetadataRepositoryCoordinatorService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SqliteMetadataRepositoryCoordinatorService();
        }
        return INSTANCE;
    }

    private SqliteMetadataRepositoryCoordinatorService() {
    }

    @Override
    public RepositoryCoordinator convert(SQLiteMetadataRepositoryCoordinatorDefinition sqLiteRepositoryCoordinatorDefinition) {
        Map<String, Database> databases = new HashMap<>();
        SqliteDatabaseConnection databaseConnection = getDatabaseConnection(sqLiteRepositoryCoordinatorDefinition,
                sqLiteRepositoryCoordinatorDefinition.getOwner());
        SqliteDatabase sqliteDatabase = new SqliteDatabase(databaseConnection);
        databases.put("owner", sqliteDatabase);
        databases.put("writer", sqliteDatabase);
        databases.put("reader", sqliteDatabase);

        return new RepositoryCoordinator(databases);
    }

    @Override
    public SqliteDatabaseConnection getDatabaseConnection(SQLiteMetadataRepositoryCoordinatorDefinition sqLiteRepositoryCoordinatorDefinition, MetadataRepositoryCoordinatorProfileDefinition metadataRepositoryCoordinatorProfileDefinition) {
        if (sqLiteRepositoryCoordinatorDefinition.getConnection().isPresent()) {
            return new SqliteDatabaseConnection(
                    FrameworkControl.getInstance().resolveConfiguration(sqLiteRepositoryCoordinatorDefinition.getConnection().get()),
                    "",
                    "",
                    sqLiteRepositoryCoordinatorDefinition.getInitSql());
        } else {
            return new SqliteDatabaseConnection(
                    FrameworkControl.getInstance().resolveConfiguration(sqLiteRepositoryCoordinatorDefinition.getFile()),
                    sqLiteRepositoryCoordinatorDefinition.getInitSql());
        }
    }

    @Override
    public Class<SQLiteMetadataRepositoryCoordinatorDefinition> appliesTo() {
        return SQLiteMetadataRepositoryCoordinatorDefinition.class;
    }
}
