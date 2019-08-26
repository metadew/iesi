package io.metadew.iesi.connection.database.connection.db2;

import io.metadew.iesi.connection.database.connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Connection object for IBM DB2 databases. This class extends the default
 * database connection object.
 *
 * @author peter.billen
 */
public class Db2DatabaseConnection extends DatabaseConnection {

    private static String type = "db2";
    private String schema;

    public Db2DatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword);
    }

	public Db2DatabaseConnection(String hostName, int portNumber, String databaseName, String userName,
			String userPassword) {
		super(type, getConnectionUrl(hostName, portNumber, databaseName), userName, userPassword);
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

    @Override
    public String getDriver() {
        return "com.ibm.db2.jcc.DB2Driver";
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
            //connection.setSchema(schema.get());
            connection.createStatement().execute("set schema " + schema.get());
        }
        return connection;
    }
}
