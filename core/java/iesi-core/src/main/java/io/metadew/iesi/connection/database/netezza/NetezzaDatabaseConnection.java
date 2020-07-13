package io.metadew.iesi.connection.database.netezza;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NetezzaDatabaseConnection extends SchemaDatabaseConnection {

    private static String type = "netezza";

    public NetezzaDatabaseConnection(String connectionURL, String userName, String userPassword, String initSql) {
        super(type, connectionURL, userName, userPassword, initSql);
    }

    public NetezzaDatabaseConnection(String connectionURL, String userName, String userPassword, String initSql, String schema) {
        super(type, connectionURL, userName, userPassword, initSql, schema);
    }

    public NetezzaDatabaseConnection(String hostName, int portNumber, String databaseName, String userName, String userPassword, String initSql) {
        this(getConnectionUrl(hostName, portNumber, databaseName), userName, userPassword, initSql);
    }

    public NetezzaDatabaseConnection(String hostName, int portNumber, String databaseName, String userName, String userPassword, String initSql, String schema) {
        this(getConnectionUrl(hostName, portNumber, databaseName), userName, userPassword, initSql, schema);
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
