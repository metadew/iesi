package io.metadew.iesi.connection.database.postgresql;

import io.metadew.iesi.connection.database.connection.ISchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PostgresqlDatabaseConnectionService extends SchemaDatabaseConnectionService<PostgresqlDatabaseConnection> implements ISchemaDatabaseConnectionService<PostgresqlDatabaseConnection> {

    private static PostgresqlDatabaseConnectionService INSTANCE;

    public synchronized static PostgresqlDatabaseConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PostgresqlDatabaseConnectionService();
        }
        return INSTANCE;
    }

    private PostgresqlDatabaseConnectionService() {}
    
    @Override
    public String getDriver(PostgresqlDatabaseConnection databaseConnection) {
        return "org.postgresql.Driver";
    }

    @Override
    public Class<PostgresqlDatabaseConnection> appliesTo() {
        return PostgresqlDatabaseConnection.class;
    }
}