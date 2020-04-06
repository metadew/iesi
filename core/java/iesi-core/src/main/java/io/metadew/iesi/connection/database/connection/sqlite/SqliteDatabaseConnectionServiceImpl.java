package io.metadew.iesi.connection.database.connection.sqlite;

import com.zaxxer.hikari.HikariConfig;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionServiceImpl;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SqliteDatabaseConnectionServiceImpl extends DatabaseConnectionServiceImpl<SqliteDatabaseConnection> implements DatabaseConnectionService<SqliteDatabaseConnection> {

    private static SqliteDatabaseConnectionServiceImpl INSTANCE;

    public synchronized static SqliteDatabaseConnectionServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SqliteDatabaseConnectionServiceImpl();
        }
        return INSTANCE;
    }
    public HikariConfig configure(SqliteDatabaseConnection sqliteDatabaseConnection, HikariConfig hikariConfig) {
        super.configure(sqliteDatabaseConnection, hikariConfig);
        hikariConfig.setConnectionTestQuery("select 1");
        hikariConfig.setDriverClassName(getDriver(sqliteDatabaseConnection));
        return hikariConfig;
    }

    private SqliteDatabaseConnectionServiceImpl() {}

    @Override
    public String getDriver(SqliteDatabaseConnection databaseConnection) {
        return "org.sqlite.JDBC";
    }

    @Override
    public Class<SqliteDatabaseConnection> appliesTo() {
        return SqliteDatabaseConnection.class;
    }
}