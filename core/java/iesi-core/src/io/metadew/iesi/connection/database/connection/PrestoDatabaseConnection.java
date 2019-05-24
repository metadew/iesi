package io.metadew.iesi.connection.database.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Connection object for Presto SQL . This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class PrestoDatabaseConnection extends DatabaseConnection {

    private static String type = "presto";
    private String schema;

    public PrestoDatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword);
    }

    public PrestoDatabaseConnection(String hostName, int portNumber, String catalogName, String schemaName, String userName, String userPassword) {
        super(type, getConnectionUrl(hostName, portNumber, catalogName, schemaName, userName, userPassword), userName, userPassword);
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

    @Override
    public String getDriver() {
        return "io.prestosql.jdbc.PrestoDriver";
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    @SuppressWarnings("unused")
    private Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }

    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        return connection;
    }
}
