package io.metadew.iesi.metadata_repository.repository.database.connection;

/**
 * Connection object for SQLite databases. This class extends the default database connection object.
 * 
 * @author peter.billen
 *
 */
public class SqliteDatabaseConnection extends DatabaseConnection {

	private static String type = "sqlite";

	public SqliteDatabaseConnection(String connectionURL, String userName, String userPassword) {
		super(type, connectionURL, userName, userPassword);
		System.out.println(connectionURL);
	}

	public SqliteDatabaseConnection(String fileName) {
		super(type, "jdbc:sqlite:" + fileName, "", "");
		System.out.println("jdbc:sqlite:" + fileName);
	}


	@Override
	public String getDriver() {
		return "org.sqlite.JDBC";
	}
}
