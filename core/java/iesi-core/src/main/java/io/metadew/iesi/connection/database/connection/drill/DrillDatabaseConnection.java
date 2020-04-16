package io.metadew.iesi.connection.database.connection.drill;

import io.metadew.iesi.connection.database.connection.SchemaDatabaseConnection;

/**
 * Connection object for Apache Drill . This class extends the default database connection object.
 * 
 * @author peter.billen
 *
 */
public class DrillDatabaseConnection extends SchemaDatabaseConnection {

	private static String type = "drill";

	public DrillDatabaseConnection(String connectionURL, String userName, String userPassword) {
		super(type, connectionURL, userName, userPassword, null);
	}

	public DrillDatabaseConnection(String connectionURL, String userName, String userPassword, String schema) {
		super(type, connectionURL, userName, userPassword, null, schema);
	}

	public DrillDatabaseConnection(String connectionMode, String clusterNames, String directoryName, String clusterId, String schemaName, String triesParameter, String userName, String userPassword) {
		this(getConnectionUrl(connectionMode, clusterNames, directoryName, clusterId, schemaName, triesParameter), userName, userPassword);
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

}
