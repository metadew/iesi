package io.metadew.iesi.connection.database.connection.db2;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Db2DatabaseConnection extends SchemaDatabaseConnection {

    private static String type = "db2";

    public Db2DatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword, null);
    }

    public Db2DatabaseConnection(String connectionURL, String userName, String userPassword, String schema) {
        super(type, connectionURL, userName, userPassword, null, schema);
    }

    public Db2DatabaseConnection(String hostName, int portNumber, String databaseName, String userName,
                                 String userPassword) {
        this(getConnectionUrl(hostName, portNumber, databaseName), userName, userPassword);
    }

    public static String getConnectionUrl(String hostName, int portNumber, String databaseName) {
        StringBuilder connectionUrl = new StringBuilder();
        connectionUrl.append("jdbc:db2://");
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
