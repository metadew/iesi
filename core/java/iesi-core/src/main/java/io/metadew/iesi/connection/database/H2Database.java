package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.database.connection.h2.H2DatabaseConnection;

/**
 * Database object for H2 databases
 *
 * @author peter.billen
 */
public class H2Database extends SchemaDatabase {

    public H2Database(H2DatabaseConnection databaseConnection, String schema) {
        super(databaseConnection, schema);
    }

    public H2Database(H2DatabaseConnection databaseConnection) {
        super(databaseConnection);
    }

}
