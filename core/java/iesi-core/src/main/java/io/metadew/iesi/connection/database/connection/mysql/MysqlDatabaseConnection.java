package io.metadew.iesi.connection.database.connection.mysql;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;
import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;

/**
 * Connection object for MySQL databases. This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class MysqlDatabaseConnection extends SchemaDatabaseConnection {

    private static String type = "mysql";

    public MysqlDatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword);
    }

    public MysqlDatabaseConnection(String hostName, int portNumber, String schemaName, String userName,
                                   String userPassword) {
        super(type, getConnectionUrl(hostName, portNumber, schemaName), userName, userPassword);
    }

    public static String getConnectionUrl(String hostName, int portNumber, String schemaName) {
        StringBuilder connectionUrl = new StringBuilder();
        connectionUrl.append("jdbc:mysql://");
        connectionUrl.append(hostName);
        if (portNumber > 0) {
            connectionUrl.append(":");
            connectionUrl.append(portNumber);
        }

        if (!schemaName.isEmpty()) {
            connectionUrl.append("/");
            connectionUrl.append(schemaName);
        }

        return connectionUrl.toString();
    }

    @Override
    public String getDriver() {
        return "com.mysql.jdbc.Driver";
    }
}
