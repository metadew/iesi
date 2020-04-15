package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.temporary.TemporaryDatabaseConnection;

public class TemporaryDatabase extends Database {

    public TemporaryDatabase(TemporaryDatabaseConnection databaseConnection) {
        super(databaseConnection);
    }
}