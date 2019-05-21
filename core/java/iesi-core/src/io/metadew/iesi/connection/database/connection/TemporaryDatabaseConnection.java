package io.metadew.iesi.connection.database.connection;

/**
 * Connection object for Temporary SQLite databases. This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class TemporaryDatabaseConnection extends DatabaseConnection {

    public TemporaryDatabaseConnection(String type, String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword);
    }

    @Override
    public String getDriver() {
        return "";
    }
}
