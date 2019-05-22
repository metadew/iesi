package io.metadew.iesi.connection.database.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Connection object for Apache Drill . This class extends the default database connection object.
 * 
 * @author peter.billen
 *
 */
public class DrillDatabaseConnection extends DatabaseConnection {

	private static String type = "drill";
	private String schema;

	public DrillDatabaseConnection(String connectionURL, String userName, String userPassword) {
		super(type, connectionURL, userName, userPassword);
	}

	public DrillDatabaseConnection(String connectionMode, String clusterNames, String directoryName, String clusterId, String schemaName, String triesParameter, String userName, String userPassword) {
		super(type, getConnectionUrl(connectionMode, clusterNames, directoryName, clusterId, schemaName, triesParameter), userName, userPassword);
	}
	
private static String getConnectionUrl(String connectionMode, String clusterNames, String directoryName, String clusterId, String schemaName, String triesParameter) {
		StringBuilder connectionUrl = new StringBuilder();
		connectionUrl.append("jdbc:drill:");
		if (connectionMode.equalsIgnoreCase("drillbit")) {
			connectionUrl.append("drillbit=");
		} else {
			connectionUrl.append("zk=");
		}
		
		connectionUrl.append(clusterNames);
		if (!directoryName.isEmpty()) {
			connectionUrl.append("/");
			connectionUrl.append(directoryName);
		}
		
		if (!clusterId.isEmpty()) {
			connectionUrl.append("/");
			connectionUrl.append(clusterId);
		}
		
		if (!schemaName.isEmpty()) {
			connectionUrl.append(";schema=");
			connectionUrl.append(schemaName);
		}
		
		if (!triesParameter.isEmpty()) {
			connectionUrl.append(";tries=");
			connectionUrl.append(triesParameter);
		}

		return connectionUrl.toString();
	}

	@Override
	public String getDriver() {
		return "org.apache.drill.jdbc.Driver";
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
