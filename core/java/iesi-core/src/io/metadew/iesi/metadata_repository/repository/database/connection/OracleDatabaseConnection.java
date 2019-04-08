package io.metadew.iesi.metadata_repository.repository.database.connection;

/**
 * Connection object for Oracle databases. This class extends the default database connection object.
 * 
 * @author peter.billen
 *
 */
public class OracleDatabaseConnection extends DatabaseConnection {

	private static String type = "oracle";

	public OracleDatabaseConnection(String connectionURL, String userName, String userPassword) {
		super(type, connectionURL, userName, userPassword);
		System.getProperties().setProperty("oracle.jdbc.J2EE13Compliant", "true");
	}

	@Override
	public String getDriver() {
		return "oracle.jdbc.driver.OracleDriver";
	}
}
