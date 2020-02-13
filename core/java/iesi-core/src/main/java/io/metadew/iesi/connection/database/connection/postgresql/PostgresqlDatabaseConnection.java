package io.metadew.iesi.connection.database.connection.postgresql;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Connection object for Postgresql databases. This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class PostgresqlDatabaseConnection extends SchemaDatabaseConnection {

    private static String type = "postgresql";

    public PostgresqlDatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword);
    }

    public PostgresqlDatabaseConnection(String connectionURL, String userName, String userPassword, String schema) {
        super(type, connectionURL, userName, userPassword, schema);
    }

    public PostgresqlDatabaseConnection(String hostName, int portNumber, String databaseName, String userName,
                                        String userPassword) {
        super(type, getConnectionUrl(hostName, portNumber, databaseName), userName, userPassword);
    }

    public static String getConnectionUrl(String hostName, int portNumber, String databaseName) {
        StringBuilder connectionUrl = new StringBuilder();
        connectionUrl.append("jdbc:postgresql://");
        connectionUrl.append(hostName);
        if (portNumber > 0) {
            connectionUrl.append(":");
            connectionUrl.append(portNumber);
        }

        if (!databaseName.isEmpty()) {
            connectionUrl.append("/");
            connectionUrl.append(databaseName);
        }

        return connectionUrl.toString();
    }

    @Override
    public String getDriver() {
        return "org.postgresql.Driver";
    }

}
