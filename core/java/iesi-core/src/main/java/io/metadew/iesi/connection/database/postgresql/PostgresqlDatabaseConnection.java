package io.metadew.iesi.connection.database.postgresql;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PostgresqlDatabaseConnection extends SchemaDatabaseConnection {

    private static String type = "postgresql";

    public PostgresqlDatabaseConnection(String connectionURL, String userName, String userPassword, String initSql) {
        super(type, connectionURL, userName, userPassword, initSql);
    }

    public PostgresqlDatabaseConnection(String connectionURL, String userName, String userPassword, String initSql, String schema) {
        super(type, connectionURL, userName, userPassword, initSql, schema);
    }

    public PostgresqlDatabaseConnection(String hostName, int portNumber, String databaseName, String userName,
                                        String userPassword, String initSql) {
        this(getConnectionUrl(hostName, portNumber, databaseName), userName, userPassword, initSql);
    }

    public PostgresqlDatabaseConnection(String hostName, int portNumber, String databaseName, String schema, String userName,
                                        String userPassword, String initSql) {
        this(getConnectionUrl(hostName, portNumber, databaseName), userName, userPassword, initSql, schema);
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

}
