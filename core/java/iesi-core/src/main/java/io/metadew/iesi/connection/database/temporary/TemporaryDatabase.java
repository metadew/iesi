package io.metadew.iesi.connection.database.temporary;

import io.metadew.iesi.connection.database.Database;

public class TemporaryDatabase extends Database {

    public TemporaryDatabase(TemporaryDatabaseConnection databaseConnection) {
        super(databaseConnection);
    }
}