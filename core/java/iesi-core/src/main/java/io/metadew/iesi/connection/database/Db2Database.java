package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.db2.Db2DatabaseConnection;

public class Db2Database extends SchemaDatabase {

    public Db2Database(Db2DatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }

}
