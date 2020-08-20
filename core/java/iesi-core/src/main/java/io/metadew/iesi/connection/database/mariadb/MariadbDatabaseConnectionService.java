package io.metadew.iesi.connection.database.mariadb;

import io.metadew.iesi.connection.database.connection.DatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.IDatabaseConnectionService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MariadbDatabaseConnectionService extends DatabaseConnectionService<MariadbDatabaseConnection> implements IDatabaseConnectionService<MariadbDatabaseConnection> {

    private static MariadbDatabaseConnectionService INSTANCE;

    public synchronized static MariadbDatabaseConnectionService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MariadbDatabaseConnectionService();
        }
        return INSTANCE;
    }

    private MariadbDatabaseConnectionService() {
    }

    //    org.mariadb.jdbc.Driver
    @Override
    public String getDriver(MariadbDatabaseConnection databaseConnection) {
        return "org.mariadb.jdbc.Driver";
    }

    @Override
    public Class<MariadbDatabaseConnection> appliesTo() {
        return MariadbDatabaseConnection.class;
    }
}