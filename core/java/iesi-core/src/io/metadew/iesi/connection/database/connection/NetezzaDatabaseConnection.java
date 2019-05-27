package io.metadew.iesi.connection.database.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Connection object for Netezza databases. This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class NetezzaDatabaseConnection extends DatabaseConnection {

    private static String type = "netezza";
    private String schema;

    public NetezzaDatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword);
    }

    public NetezzaDatabaseConnection(String hostName, int portNumber, String databaseName, String userName, String userPassword) {
        super(type,getConnectionUrl(hostName, portNumber, databaseName), userName, userPassword);
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
	
    @Override
    public String getDriver() {
        return "org.netezza.Driver";
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    private Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }

    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        Optional<String> schema = getSchema();
        if (schema.isPresent()) {
            connection.setSchema(schema.get());

        }
        return connection;
    }
}
