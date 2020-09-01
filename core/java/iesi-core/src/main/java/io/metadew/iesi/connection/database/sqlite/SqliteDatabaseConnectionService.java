package io.metadew.iesi.connection.database.sqlite;

import com.zaxxer.hikari.HikariConfig;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.IDatabaseConnectionService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SqliteDatabaseConnectionService extends DatabaseConnectionService<SqliteDatabaseConnection> implements IDatabaseConnectionService<SqliteDatabaseConnection> {

    private static SqliteDatabaseConnectionService INSTANCE;

    public synchronized static SqliteDatabaseConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SqliteDatabaseConnectionService();
        }
        return INSTANCE;
    }

    public HikariConfig configure(SqliteDatabaseConnection sqliteDatabaseConnection, HikariConfig hikariConfig) {
        super.configure(sqliteDatabaseConnection, hikariConfig);
        hikariConfig.setConnectionTestQuery("select 1");
        hikariConfig.setDriverClassName(getDriver(sqliteDatabaseConnection));
        return hikariConfig;
    }

    private SqliteDatabaseConnectionService() {
    }

    @Override
    public String getDriver(SqliteDatabaseConnection databaseConnection) {
        return "org.sqlite.JDBC";
    }

    @Override
    public Class<SqliteDatabaseConnection> appliesTo() {
        return SqliteDatabaseConnection.class;
    }
}