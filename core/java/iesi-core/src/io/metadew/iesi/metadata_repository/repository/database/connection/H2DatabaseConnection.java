package io.metadew.iesi.metadata_repository.repository.database.connection;

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
