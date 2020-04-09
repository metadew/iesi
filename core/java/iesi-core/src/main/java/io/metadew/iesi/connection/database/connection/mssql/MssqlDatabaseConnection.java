package io.metadew.iesi.connection.database.connection.mssql;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MssqlDatabaseConnection extends SchemaDatabaseConnection {

    private static String type = "mssql";

    public MssqlDatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword, null);
    }


    public MssqlDatabaseConnection(String connectionURL, String userName, String userPassword, String schema) {
        super(type, connectionURL, userName, userPassword, schema);
    }

    public MssqlDatabaseConnection(String hostName, int portNumber, String databaseName, String userName,
                                   String userPassword, String schema) {
        this(getConnectionUrl(hostName, portNumber, databaseName), userName, userPassword, schema);
    }

    public static String getConnectionUrl(String hostName, int portNumber, String databaseName) {
        StringBuilder connectionUrl = new StringBuilder();
        connectionUrl.append("jdbc:sqlserver://");
        connectionUrl.append(hostName);
        if (portNumber > 0) {
            connectionUrl.append(":");
            connectionUrl.append(portNumber);
        }

        if (!databaseName.isEmpty()) {
            connectionUrl.append(";");
            connectionUrl.append("database=");
            connectionUrl.append(databaseName);
        }

        /*
         * connectionUrl.append(";"); connectionUrl.append("encrypt=");
         * connectionUrl.append("true"); connectionUrl.append(";");
         *
         * connectionUrl.append("trustServerCertificate=");
         * connectionUrl.append("false"); connectionUrl.append(";");
         *
         * connectionUrl.append("loginTimeout="); connectionUrl.append("30");
         * connectionUrl.append(";");
         */

        return connectionUrl.toString();
    }

}
