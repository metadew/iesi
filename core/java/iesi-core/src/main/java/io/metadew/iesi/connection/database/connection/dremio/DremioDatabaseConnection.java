package io.metadew.iesi.connection.database.connection.dremio;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;

/**
 * Connection object for Dremio . This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class DremioDatabaseConnection extends SchemaDatabaseConnection {

    private static String type = "dremio";

    public DremioDatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword);
    }

    public DremioDatabaseConnection(String connectionURL, String userName, String userPassword, String schema) {
        super(type, connectionURL, userName, userPassword, schema);
    }

    public DremioDatabaseConnection(String hostName, int portNumber, String connectionMode, String clusterName, String schemaName, String userName, String userPassword) {
        super(type, getConnectionUrl(hostName, portNumber, connectionMode, clusterName, schemaName, userName, userPassword), userName, userPassword);
    }

    private static String getConnectionUrl(String hostName, int portNumber, String connectionMode, String clusterName, String schemaName, String userName, String userPassword) {
        StringBuilder connectionUrl = new StringBuilder();
        connectionUrl.append("jdbc:dremio:");
        if (connectionMode.equalsIgnoreCase("zookeeper")) {
            connectionUrl.append("zk");
        } else if (connectionMode.equalsIgnoreCase("direct")) {
            connectionUrl.append("direct");
        } else {
            throw new RuntimeException("dremio.connection.mode.unknown");
        }
        connectionUrl.append("=");
        connectionUrl.append(hostName);
        if (portNumber > 0) {
            connectionUrl.append(":");
            connectionUrl.append(portNumber);
        }

        if (connectionMode.equalsIgnoreCase("zookeeper")) {
            if (!clusterName.isEmpty()) {
                connectionUrl.append(clusterName);
            }
        }

        if (!schemaName.isEmpty()) {
            connectionUrl.append(";schema=");
            connectionUrl.append(schemaName);
        }

        return connectionUrl.toString();
    }

    @Override
    public String getDriver() {
        return "com.dremio.jdbc.Driver";
    }

}
