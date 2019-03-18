package io.metadew.iesi.connection.database;

import io.metadew.iesi.connection.DatabaseConnection;

/**
 * Connection object for Teradata databases. This class extends the default database connection object.
 * 
 * @author peter.billen
 *
 */
public class TeradataDatabaseConnection extends DatabaseConnection {

	private static String type = "teradata";

	public TeradataDatabaseConnection(String connectionURL, String userName, String userPassword) {
		super(type, connectionURL, userName, userPassword);
	}

	public TeradataDatabaseConnection(String hostName, int portNumber, String databaseName, String userName,
			String userPassword) {
		super(type, "jdbc:teradata://" + hostName + "/" + "DATABASE=" + databaseName, userName, userPassword);
	}
}
