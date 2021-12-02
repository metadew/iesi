package io.metadew.iesi.connection.database.generic;


import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;

/**
 * Connection object for generic database. This class extends the default
 * database connection object.
 *
 * @author suyash.d.jain
 *
 */

public class GenericDatabaseConnection extends SchemaDatabaseConnection {

    private static String type = "db.generic";

    public GenericDatabaseConnection(String connectionURL, String userName, String userPassword, String connectionInitSql) {
        super(type, connectionURL, userName, userPassword, connectionInitSql);
    }

    public GenericDatabaseConnection(String connectionURL, String userName, String userPassword, String connectionInitSql, String schema) {
        super(type, connectionURL, userName, userPassword, connectionInitSql, schema);
    }
}
