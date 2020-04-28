package io.metadew.iesi.connection.database.presto;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;

/**
 * Connection object for Presto SQL . This class extends the default database connection object.
 *
 * @author peter.billen
 */
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PrestoDatabaseConnection extends SchemaDatabaseConnection {

    private static String type = "presto";

    public PrestoDatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword, null);
    }

    public PrestoDatabaseConnection(String connectionURL, String userName, String userPassword, String schema) {
        super(type, connectionURL, userName, userPassword, null, schema);
    }

    public PrestoDatabaseConnection(String hostName, int portNumber, String catalogName, String schemaName, String userName, String userPassword) {
        this(getConnectionUrl(hostName, portNumber, catalogName, schemaName, userName, userPassword), userName, userPassword, schemaName);
    }

    private static String getConnectionUrl(String hostName, int portNumber, String catalogName, String schemaName, String userName, String userPassword) {
        StringBuilder connectionUrl = new StringBuilder();
        connectionUrl.append("jdbc:presto://");
        connectionUrl.append(hostName);
        if (portNumber > 0) {
            connectionUrl.append(":");
            connectionUrl.append(portNumber);
        }

        if (!catalogName.isEmpty()) {
            connectionUrl.append("/");
            connectionUrl.append(catalogName);
        }

        if (!schemaName.isEmpty()) {
            connectionUrl.append("/");
            connectionUrl.append(schemaName);
        }

        return connectionUrl.toString();
    }

}
