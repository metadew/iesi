package io.metadew.iesi.connection.database.generic;


import io.metadew.iesi.connection.database.connection.DatabaseConnection;

/**
 * Connection object for generic database. This class extends the default
 * database connection object.
 *
 * @author suyash.d.jain
 *
 */

public class GenericDatabaseConnection extends DatabaseConnection {

    private static String type = "generic";

    public GenericDatabaseConnection(String connectionURL, String userName, String userPassword, String connectionInitSql) {
        super(type, connectionURL, userName, userPassword, connectionInitSql);
    }
}
