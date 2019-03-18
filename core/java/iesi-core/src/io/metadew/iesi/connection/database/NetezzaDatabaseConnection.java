package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.DatabaseConnection;

/**
 * Connection object for Netezza databases. This class extends the default database connection object.
 * 
 * @author peter.billen
 *
 */
public class NetezzaDatabaseConnection extends DatabaseConnection {

	private static String type = "netezza";

	public NetezzaDatabaseConnection(String connectionURL, String userName, String userPassword) {
		super(type, connectionURL, userName, userPassword);
	}
	
	public NetezzaDatabaseConnection(String hostName, int portNumber, String databaseName, String userName, String userPassword) {
		super(type, "jdbc:netezza://" + hostName + ":" + portNumber + "/" + databaseName, userName, userPassword);
	}

}
