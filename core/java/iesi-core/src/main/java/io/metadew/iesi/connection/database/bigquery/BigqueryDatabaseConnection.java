package io.metadew.iesi.connection.database.bigquery;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BigqueryDatabaseConnection extends SchemaDatabaseConnection {

    private static String type = "bigquery";

    //TODO add relevant parameters
    public BigqueryDatabaseConnection(String connectionURL, String userName, String userPassword, String initSql) {
        super(type, connectionURL, userName, userPassword, initSql);
    }

    public BigqueryDatabaseConnection(String connectionURL, String userName, String userPassword, String initSql, String schema) {
        super(type, connectionURL, userName, userPassword, initSql, schema);
    }

    public BigqueryDatabaseConnection(String hostName, int portNumber, String databaseName, String userName,
                                        String userPassword, String initSql) {
        this(getConnectionUrl(hostName, portNumber, databaseName), userName, userPassword, initSql);
    }

    public BigqueryDatabaseConnection(String hostName, int portNumber, String databaseName, String schema, String userName,
                                        String userPassword, String initSql) {
        this(getConnectionUrl(hostName, portNumber, databaseName), userName, userPassword, initSql, schema);
    }

    public static String getConnectionUrl(String hostName, int portNumber, String databaseName) {
        StringBuilder connectionUrl = new StringBuilder();
        connectionUrl.append("jdbc:bigquery://");
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
