package io.metadew.iesi.connection.database.mysql;

import io.metadew.iesi.connection.database.connection.DatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.IDatabaseConnectionService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MysqlDatabaseConnectionService extends DatabaseConnectionService<MysqlDatabaseConnection> implements IDatabaseConnectionService<MysqlDatabaseConnection> {

    private static MysqlDatabaseConnectionService INSTANCE;

    public synchronized static MysqlDatabaseConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MysqlDatabaseConnectionService();
        }
        return INSTANCE;
    }

    private MysqlDatabaseConnectionService() {}

    @Override
    public String getDriver(MysqlDatabaseConnection databaseConnection) {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public Class<MysqlDatabaseConnection> appliesTo() {
        return MysqlDatabaseConnection.class;
    }

}