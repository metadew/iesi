package io.metadew.iesi.connection.database.connection.postgresql;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnectionServiceImpl;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PostgresqlDatabaseConnectionServiceImpl extends SchemaDatabaseConnectionServiceImpl<PostgresqlDatabaseConnection> implements SchemaDatabaseConnectionService<PostgresqlDatabaseConnection> {

    private static PostgresqlDatabaseConnectionServiceImpl INSTANCE;

    public synchronized static PostgresqlDatabaseConnectionServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PostgresqlDatabaseConnectionServiceImpl();
        }
        return INSTANCE;
    }

    private PostgresqlDatabaseConnectionServiceImpl() {}
    
    @Override
    public String getDriver(PostgresqlDatabaseConnection databaseConnection) {
        return "org.postgresql.Driver";
    }

    @Override
    public Class<PostgresqlDatabaseConnection> appliesTo() {
        return PostgresqlDatabaseConnection.class;
    }
}