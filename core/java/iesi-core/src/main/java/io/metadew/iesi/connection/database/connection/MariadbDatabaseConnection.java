package io.metadew.iesi.connection.database.connection;

/**
 * Connection object for Maria DB databases. This class extends the default
 * database connection object.
 * 
 * @author peter.billen
 *
 */
public class MariadbDatabaseConnection extends DatabaseConnection {

	private static String type = "mariadb";

	public MariadbDatabaseConnection(String connectionURL, String userName, String userPassword) {
		super(type, connectionURL, userName, userPassword);
	}

	public MariadbDatabaseConnection(String hostName, int portNumber, String databaseName, String userName,
			String userPassword) {
		super(type, getConnectionUrl(hostName, portNumber, databaseName), userName, userPassword);
	}

	@Override
	public String getDriver() {
		return "org.mariadb.jdbc.Driver";
	}

	public static String getConnectionUrl(String hostName, int portNumber, String databaseName) {
		StringBuilder connectionUrl = new StringBuilder();
		connectionUrl.append("jdbc:mariadb://");
		connectionUrl.append(hostName);
		if (portNumber > 0) {
			connectionUrl.append(":");
			connectionUrl.append(portNumber);
		}
		connectionUrl.append("/");
		connectionUrl.append(databaseName);

		return connectionUrl.toString();
	}

}
