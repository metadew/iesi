package io.metadew.iesi.connection.database.connection.h2;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;

/**
 * Connection object for H2 databases. This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class H2DatabaseConnection extends SchemaDatabaseConnection {

    private static String type = "h2";

    public H2DatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword);
    }

    public H2DatabaseConnection(String connectionURL, String userName, String userPassword, String schema) {
        super(type, connectionURL, userName, userPassword, schema);
    }

}
