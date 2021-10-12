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

    //TODO : What should be the type because database is generic
    @Override
    public String getDriver(GenericDatabaseConnection databaseConnection) {
        return null;
    }

    @Override
    public Class appliesTo() {
        return null;
    }
}
