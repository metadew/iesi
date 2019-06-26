package io.metadew.iesi.connection.database.connection;

/**
 * Connection object for SQLite databases. This class extends the default database connection object.
 *
 * @author peter.billen
 */
public class SqliteDatabaseConnection extends DatabaseConnection {

    private static String type = "sqlite";

    public SqliteDatabaseConnection(String connectionURL, String userName, String userPassword) {
        super(type, connectionURL, userName, userPassword);
    }

    public SqliteDatabaseConnection(String fileName) {
        super(type, getConnectionUrl(fileName), "", "");
    }

	public static String getConnectionUrl(String fileName) {
		StringBuilder connectionUrl = new StringBuilder();
		connectionUrl.append("jdbc:sqlite:");
		connectionUrl.append(fileName);

		return connectionUrl.toString();
	}

    @Override
    public String getDriver() {
        return "org.sqlite.JDBC";
    }


}
