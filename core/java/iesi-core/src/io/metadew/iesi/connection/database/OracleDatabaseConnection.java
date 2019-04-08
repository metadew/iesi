//package io.metadew.iesi.connection.database;
//
//import io.metadew.iesi.connection.DatabaseConnection;
//
///**
// * Connection object for Oracle databases. This class extends the default database connection object.
// *
// * @author peter.billen
// *
// */
//public class OracleDatabaseConnection extends DatabaseConnection {
//
//	private static String type = "oracle";
//
//	public OracleDatabaseConnection(String connectionURL, String userName, String userPassword) {
//		super(type, connectionURL, userName, userPassword);
//		this.setProperties();
//	}
//
//	public OracleDatabaseConnection(String hostName, int portNumber, String tnsAlias, String userName, String userPassword, String serviceName) {
//		super(type, "jdbc:oracle:thin:@" + hostName + ":" + portNumber + ":" + tnsAlias, userName, userPassword);
//
//		// Update for service name use
//		// Both tns alias and service name are both in use
//		if (!serviceName.equals("") ) {
//			this.setConnectionURL("jdbc:oracle:thin:@//" + hostName + ":" + portNumber + "/" + serviceName);
//		}
//
//		this.setProperties();
//	}
//
//	private void setProperties() {
//		System.getProperties().setProperty("oracle.jdbc.J2EE13Compliant", "true");
//	}
//
//	@Override
//	public String getSystemTimestampExpression() {
//		return "systimestamp";
//	}
//
//	@Override
//	public String getAllTablesQuery() {
//		return "select OWNER, TABLE_NAME from ALL_TABLES where owner = '"
//				+ this.getMetadataRepository().getMetadataTableConfiguration().getSchema() + "' and TABLE_NAME like '"
//				+ this.getMetadataRepository().getMetadataTableConfiguration().getTableNamePrefix()
//				+ this.getMetadataRepository().getMetadataRepositoryCategoryConfiguration().getPrefix()
//				+ "%' order by TABLE_NAME ASC";
//	}
//}
