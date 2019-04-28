//package io.metadew.iesi.data.configuration;
//
//import java.io.InputStream;
//
//import javax.sql.rowset.CachedRowSet;
//
//import io.metadew.iesi.common.config.ConfigFile;
//import io.metadew.iesi.connection.database.sql.SqlScriptResult;
//import io.metadew.iesi.connection.operation.ConnectionOperation;
//import io.metadew.iesi.framework.execution.FrameworkExecution;
//import io.metadew.iesi.metadata.configuration.ConnectionConfiguration;
//import io.metadew.iesi.metadata.configuration.RepositoryConfiguration;
//import io.metadew.iesi.metadata.configuration.RepositoryInstanceConfiguration;
//import io.metadew.iesi.metadata.definition.Connection;
//import io.metadew.iesi.metadata.definition.Repository;
//import io.metadew.iesi.metadata.definition.RepositoryInstance;
//import io.metadew.iesi.metadata.definition.RepositoryInstanceParameter;
//import io.metadew.iesi.metadata_repository.repository.database.connection.DatabaseConnection;
//
//public class DataRepositoryConfiguration {
//
//	private FrameworkExecution frameworkExecution;
//	private String repositoryName;
//	private String repositoryInstanceName;
//	private String repositoryInstanceLabels;
//	private Repository repository;
//	private RepositoryInstance repositoryInstance;
//	private String repositoryInstanceConnectionName;
//	private String repositoryInstancePrefix;
//	private String repositoryCategoryPrefix = "DAT_";
//	private String repositoryTableNamePrefix;
//	private String schema;
//	private DatabaseConnection databaseConnection;
//	private ConfigFile configFile;
//
//	public DataRepositoryConfiguration(FrameworkExecution frameworkExecution, String repositoryName,
//			String repositoryInstanceName, String repositoryInstanceLabels, String environmentName) {
//		this.setFrameworkExecution(frameworkExecution);
//		this.setRepositoryName(repositoryName);
//		this.setRepositoryInstanceName(repositoryInstanceName);
//		this.setRepositoryInstanceLabels(repositoryInstanceLabels);
//
//		RepositoryConfiguration repositoryConfiguration = new RepositoryConfiguration(this.getFrameworkExecution());
//		this.setRepository(repositoryConfiguration.getRepository(this.getRepositoryName()));
//		RepositoryInstanceConfiguration repositoryInstanceConfiguration = new RepositoryInstanceConfiguration(this.getFrameworkExecution());
//		this.setRepositoryInstance(repositoryInstanceConfiguration.getRepositoryInstance(this.getRepository(), this.getRepositoryInstanceName()));
//
//		this.setRepositoryInstanceName("");
//		this.setRepositoryInstancePrefix("");
//		this.setSchema("");
//		for (RepositoryInstanceParameter repositoryInstanceParameter : this.getRepositoryInstance().getParameters()) {
//			if (repositoryInstanceParameter.getName().equalsIgnoreCase("connection")) {
//				this.setRepositoryInstanceConnectionName(repositoryInstanceParameter.getValue());
//			} else if (repositoryInstanceParameter.getName().equalsIgnoreCase("prefix")) {
//				this.setRepositoryInstancePrefix(repositoryInstanceParameter.getValue());
//			} else if (repositoryInstanceParameter.getName().equalsIgnoreCase("schema")) {
//				this.setSchema(repositoryInstanceParameter.getValue());
//			}
//		}
//
//		// Connection
//		ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(this.getFrameworkExecution());
//		Connection connection = connectionConfiguration.getConnection(this.getRepositoryInstanceConnectionName(),
//				environmentName).get();
//		ConnectionOperation connectionOperation = new ConnectionOperation(this.getFrameworkExecution());
//		this.setDatabaseConnection(connectionOperation
//				.getDatabaseConnection(connection));
//
//		// Create config file
//		ConfigFile configFile = new ConfigFile();
//		configFile.setProperty("iesi.metadata.repository.name", this.getRepositoryName());
//		configFile.setProperty("iesi.metadata.repository.type", connection.getType());
//		configFile.setProperty("iesi.metadata.repository.category", "");
//		configFile.setProperty("iesi.metadata.repository.scope", "");
//		configFile.setProperty("iesi.metadata.repository.instance.name", "");
//		configFile.setProperty("iesi.metadata.repository.sqlite.file", "C:/Data/repo.db3");
//
//		this.setConfigFile(configFile);
//
//	}
//
//
//
//	// database
//	public CachedRowSet executeQuery(String query) {
//		CachedRowSet crs = null;
//		try {
//			crs = this.getDatabaseConnection().executeQuery(query);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		return crs;
//	}
//
//	public void executeUpdate(String query) {
//		this.getDatabaseConnection().executeUpdate(query);
//	}
//
//	public void executeScript(String fileName) {
//		SqlScriptResult dcSQLScriptResult = this.getDatabaseConnection().executeScript(fileName);
//
//		if (dcSQLScriptResult.getReturnCode() != 0) {
//			throw new RuntimeException("Error execting SQL script");
//		}
//	}
//
//	public void executeScript(InputStream inputStream) {
//		SqlScriptResult dcSQLScriptResult = this.getDatabaseConnection().executeScript(inputStream);
//
//		if (dcSQLScriptResult.getReturnCode() != 0) {
//			throw new RuntimeException("Error executing SQL script");
//		}
//	}
//
//	public void dropTable(String schemaName, String tableName) {
//		String queryDropTable = "";
//		if (schemaName.equalsIgnoreCase("")) {
//			queryDropTable = "drop table " + tableName;
//		} else {
//			queryDropTable = "drop table " + schemaName + "." + tableName;
//		}
//		this.getDatabaseConnection().executeUpdate(queryDropTable);
//	}
//
//	public void cleanTable(String schemaName, String tableName) {
//		String queryCleanTable = "";
//		if (schemaName.equalsIgnoreCase("")) {
//			queryCleanTable = "delete from " + tableName;
//		} else {
//			queryCleanTable = "delete from " + schemaName + "." + tableName;
//		}
//		this.getDatabaseConnection().executeUpdate(queryCleanTable);
//	}
//
//	public String getSystemTimestampExpression() {
//		if (this.getDatabaseConnection().getType().equalsIgnoreCase("oracle")) {
//			return "systimestamp";
//		} else if (this.getDatabaseConnection().getType().equalsIgnoreCase("sqlite")) {
//			return ("datetime(CURRENT_TIMESTAMP, 'localtime')");
//		} else if (this.getDatabaseConnection().getType().equalsIgnoreCase("netezza")) {
//			return "CURRENT_TIMESTAMP";
//		} else if (this.getDatabaseConnection().getType().equalsIgnoreCase("postgresql")) {
//			return "CURRENT_TIMESTAMP";
//		} else {
//			return "";
//		}
//	}
//
//	// Getters and setters
//	public String getRepositoryName() {
//		return repositoryName;
//	}
//
//	public void setRepositoryName(String repositoryName) {
//		this.repositoryName = repositoryName;
//	}
//
//	public String getRepositoryInstanceName() {
//		return repositoryInstanceName;
//	}
//
//	public void setRepositoryInstanceName(String repositoryInstanceName) {
//		this.repositoryInstanceName = repositoryInstanceName;
//	}
//
//	public String getRepositoryInstanceLabels() {
//		return repositoryInstanceLabels;
//	}
//
//	public void setRepositoryInstanceLabels(String repositoryInstanceLabels) {
//		this.repositoryInstanceLabels = repositoryInstanceLabels;
//	}
//
//	public Repository getRepository() {
//		return repository;
//	}
//
//	public void setRepository(Repository repository) {
//		this.repository = repository;
//	}
//
//	public RepositoryInstance getRepositoryInstance() {
//		return repositoryInstance;
//	}
//
//	public void setRepositoryInstance(RepositoryInstance repositoryInstance) {
//		this.repositoryInstance = repositoryInstance;
//	}
//
//	public FrameworkExecution getFrameworkExecution() {
//		return frameworkExecution;
//	}
//
//	public void setFrameworkExecution(FrameworkExecution frameworkExecution) {
//		this.frameworkExecution = frameworkExecution;
//	}
//
//	public String getRepositoryInstanceConnectionName() {
//		return repositoryInstanceConnectionName;
//	}
//
//	public void setRepositoryInstanceConnectionName(String repositoryInstanceConnectionName) {
//		this.repositoryInstanceConnectionName = repositoryInstanceConnectionName;
//	}
//
//	public DatabaseConnection getDatabaseConnection() {
//		return databaseConnection;
//	}
//
//	public void setDatabaseConnection(DatabaseConnection databaseConnection) {
//		this.databaseConnection = databaseConnection;
//	}
//
//	public String getRepositoryInstancePrefix() {
//		return repositoryInstancePrefix;
//	}
//
//	public void setRepositoryInstancePrefix(String repositoryInstancePrefix) {
//		this.repositoryInstancePrefix = repositoryInstancePrefix;
//		this.setRepositoryTableNamePrefix(this.repositoryInstancePrefix + "_" + this.getRepositoryCategoryPrefix());
//	}
//
//	public String getRepositoryCategoryPrefix() {
//		return repositoryCategoryPrefix;
//	}
//
//	public void setRepositoryCategoryPrefix(String repositoryCategoryPrefix) {
//		this.repositoryCategoryPrefix = repositoryCategoryPrefix;
//	}
//
//	public String getRepositoryTableNamePrefix() {
//		return repositoryTableNamePrefix;
//	}
//
//	public void setRepositoryTableNamePrefix(String repositoryTableNamePrefix) {
//		this.repositoryTableNamePrefix = repositoryTableNamePrefix;
//	}
//
//	public String getSchema() {
//		return schema;
//	}
//
//	public void setSchema(String schema) {
//		this.schema = schema;
//	}
//
//
//
//	public ConfigFile getConfigFile() {
//		return configFile;
//	}
//
//
//
//	public void setConfigFile(ConfigFile configFile) {
//		this.configFile = configFile;
//	}
//
//
//
//}