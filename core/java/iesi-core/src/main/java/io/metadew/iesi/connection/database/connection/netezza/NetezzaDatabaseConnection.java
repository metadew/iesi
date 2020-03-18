package io.metadew.iesi.connection.database.connection.netezza;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;

/**
 * Connection object for Netezza databases. This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class NetezzaDatabaseConnection extends SchemaDatabaseConnection {

    private static String type = "netezza";

    public NetezzaDatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword);
    }

    public NetezzaDatabaseConnection(String connectionURL, String userName, String userPassword, String schema) {
        super(type, connectionURL, userName, userPassword, schema);
    }

    public NetezzaDatabaseConnection(String hostName, int portNumber, String databaseName, String userName, String userPassword) {
        super(type, getConnectionUrl(hostName, portNumber, databaseName), userName, userPassword);
    }

    public static String getConnectionUrl(String hostName, int portNumber, String databaseName) {
        StringBuilder connectionUrl = new StringBuilder();
        connectionUrl.append("netezza://");
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

}
