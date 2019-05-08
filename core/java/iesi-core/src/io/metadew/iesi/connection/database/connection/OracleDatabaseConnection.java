package io.metadew.iesi.connection.database.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Connection object for Oracle databases. This class extends the default database connection object.
 * 
 * @author peter.billen
 *
 */
public class OracleDatabaseConnection extends DatabaseConnection {

	private static String type = "oracle";
	private String schema;

	public OracleDatabaseConnection(String connectionURL, String userName, String userPassword) {
		super(type, connectionURL, userName, userPassword);
		System.getProperties().setProperty("oracle.jdbc.J2EE13Compliant", "true");
	}

	@Override
	public String getDriver() {
		return "oracle.jdbc.driver.OracleDriver";
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
