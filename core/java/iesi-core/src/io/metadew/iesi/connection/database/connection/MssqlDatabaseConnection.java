package io.metadew.iesi.connection.database.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Connection object for Microsoft SQL databases. This class extends the default
 * database connection object.
 *
 * @author peter.billen
 */
public class MssqlDatabaseConnection extends DatabaseConnection {

    private static String type = "mssql";
    private String schema;

    public MssqlDatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword);
    }

	public MssqlDatabaseConnection(String hostName, int portNumber, String databaseName, String userName,
			String userPassword) {
		super(type, getConnectionUrl(hostName, portNumber, databaseName), userName, userPassword);
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

    @Override
    public String getDriver() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
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
