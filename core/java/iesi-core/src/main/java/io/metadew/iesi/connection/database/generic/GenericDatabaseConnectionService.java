package io.metadew.iesi.connection.database.generic;

import io.metadew.iesi.connection.database.connection.DatabaseConnectionService;
import io.metadew.iesi.connection.database.connection.IDatabaseConnectionService;

public class GenericDatabaseConnectionService extends DatabaseConnectionService<GenericDatabaseConnection> implements IDatabaseConnectionService<GenericDatabaseConnection> {

    private static GenericDatabaseConnectionService instance;

    public static synchronized GenericDatabaseConnectionService getInstance() {
        if (instance == null) {
            instance = new GenericDatabaseConnectionService();
        }
        return instance;
    }

    @Override
    public String getDriver(GenericDatabaseConnection databaseConnection) {
        return "";
    }

    @Override
    public Class appliesTo() {
        return GenericDatabaseConnection.class;
    }
}
