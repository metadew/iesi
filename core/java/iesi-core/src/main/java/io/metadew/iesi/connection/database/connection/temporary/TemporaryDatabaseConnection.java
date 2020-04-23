package io.metadew.iesi.connection.database.connection.temporary;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;

/**
 * Connection object for Temporary SQLite databases. This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class TemporaryDatabaseConnection extends DatabaseConnection {

    public TemporaryDatabaseConnection() {
        super("", "", "", "", null);
    }

}
