package io.metadew.iesi.connection.database.connection.mysql;

import io.metadew.iesi.connection.database.connection.DatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionServiceImpl;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MysqlDatabaseConnectionServiceImpl extends DatabaseConnectionServiceImpl<MysqlDatabaseConnection> implements DatabaseConnectionService<MysqlDatabaseConnection> {

    private static MysqlDatabaseConnectionServiceImpl INSTANCE;

    public synchronized static MysqlDatabaseConnectionServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MysqlDatabaseConnectionServiceImpl();
        }
        return INSTANCE;
    }

    private MysqlDatabaseConnectionServiceImpl() {}

    @Override
    public String getDriver(MysqlDatabaseConnection databaseConnection) {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public Class<MysqlDatabaseConnection> appliesTo() {
        return MysqlDatabaseConnection.class;
    }
}