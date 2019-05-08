package io.metadew.iesi.connection.database.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Connection object for H2 databases. This class extends the default database connection object.
 * 
 * @author peter.billen
 *
 */
public class H2DatabaseConnection extends DatabaseConnection {

	private static String type = "h2";
	private String schema;

	public H2DatabaseConnection(String connectionURL, String userName, String userPassword) {
		super(type, connectionURL, userName, userPassword);
	}

	public H2DatabaseConnection(String hostName, int portNumber, String pathName, String fileName, String userName, String userPassword) {
		super(type, getConnectionUrl(hostName, portNumber, pathName, fileName, userName, userPassword), userName, userPassword);
	}
	
	private static String getConnectionUrl(String hostName, int portNumber, String pathName, String fileName, String userName, String userPassword) {
		StringBuilder connectionUrl = new StringBuilder();
		connectionUrl.append("jdbc:h2:");
		if (!hostName.isEmpty()) {
			connectionUrl.append("tcp://");
			if (portNumber > 0) {
				connectionUrl.append(":");
				connectionUrl.append(portNumber);
			}
			connectionUrl.append("/");
		}
		
		if (!pathName.isEmpty()) {
			connectionUrl.append(pathName);
			connectionUrl.append("/");
		}
		connectionUrl.append(fileName);
		return connectionUrl.toString();
	}

	@Override
	public String getDriver() {
		return "org.h2.Driver";
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
			// TODO: The old JDBC API does not support the setSchema call
			connection.createStatement().execute("alter session set current_schema=" + schema.get());
			// connection.setSchema(schema.get());
		}
		return connection;
	}
}
