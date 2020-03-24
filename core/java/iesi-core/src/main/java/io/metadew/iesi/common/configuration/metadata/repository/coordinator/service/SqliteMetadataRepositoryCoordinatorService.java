package io.metadew.iesi.common.configuration.metadata.repository.coordinator.service;

import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.sqlite.SqliteDatabaseConnection;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.RepositoryCoordinatorProfileDefinition;
import io.metadew.iesi.common.configuration.metadata.repository.coordinator.SQLiteRepositoryCoordinatorDefinition;
import io.metadew.iesi.common.FrameworkControl;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;

public class SqliteMetadataRepositoryCoordinatorService implements MetadataRepositoryCoordinatorService<SQLiteRepositoryCoordinatorDefinition, SqliteDatabaseConnection> {

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
    public RepositoryCoordinator convert(SQLiteRepositoryCoordinatorDefinition sqLiteRepositoryCoordinatorDefinition) {
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
    public SqliteDatabaseConnection getDatabaseConnection(SQLiteRepositoryCoordinatorDefinition sqLiteRepositoryCoordinatorDefinition, RepositoryCoordinatorProfileDefinition repositoryCoordinatorProfileDefinition) {
        if (sqLiteRepositoryCoordinatorDefinition.getConnection().isPresent()) {
            return new SqliteDatabaseConnection(
                    FrameworkControl.getInstance().resolveConfiguration(sqLiteRepositoryCoordinatorDefinition.getConnection().get()),
                    "",
                    "");
        } else {
            return new SqliteDatabaseConnection(FrameworkControl.getInstance().resolveConfiguration(sqLiteRepositoryCoordinatorDefinition.getFile()));
        }
    }

    @Override
    public Class<SQLiteRepositoryCoordinatorDefinition> appliesTo() {
        return SQLiteRepositoryCoordinatorDefinition.class;
    }
}
