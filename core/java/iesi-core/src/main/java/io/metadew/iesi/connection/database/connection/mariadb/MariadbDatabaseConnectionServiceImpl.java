package io.metadew.iesi.connection.database.connection.mariadb;

import io.metadew.iesi.connection.database.connection.DatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.DatabaseConnectionServiceImpl;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Log4j2
public class MariadbDatabaseConnectionServiceImpl extends DatabaseConnectionServiceImpl<MariadbDatabaseConnection> implements DatabaseConnectionService<MariadbDatabaseConnection> {

    private static MariadbDatabaseConnectionServiceImpl INSTANCE;

    public synchronized static MariadbDatabaseConnectionServiceImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MariadbDatabaseConnectionServiceImpl();
        }
        return INSTANCE;
    }

    private MariadbDatabaseConnectionServiceImpl() {}
    
    @Override
    public String getDriver(MariadbDatabaseConnection databaseConnection) {
        return "org.mariadb.jdbc.Driver";
    }

    @Override
    public Class<MariadbDatabaseConnection> appliesTo() {
        return MariadbDatabaseConnection.class;
    }

}