package io.metadew.iesi.metadata.configuration;

import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.sql.rowset.CachedRowSet;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.connection.DatabaseConnection;
import io.metadew.iesi.connection.ElasticsearchConnection;
import io.metadew.iesi.connection.FileStoreConnection;
import io.metadew.iesi.connection.database.NetezzaDatabaseConnection;
import io.metadew.iesi.connection.database.OracleDatabaseConnection;
import io.metadew.iesi.connection.database.PostgresqlDatabaseConnection;
import io.metadew.iesi.connection.database.SqliteDatabaseConnection;
import io.metadew.iesi.connection.database.sql.SqlScriptResult;
import io.metadew.iesi.connection.filestore.MetadataFileStoreConnection;
import io.metadew.iesi.framework.configuration.FrameworkConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.framework.execution.FrameworkControl;
import io.metadew.iesi.metadata.operation.MetadataTableConfiguration;

public class MetadataRepositoryConfiguration {

	private FrameworkConfiguration frameworkConfiguration;
	private FrameworkControl frameworkControl;
	private FrameworkCrypto frameworkCrypto;
	private DatabaseConnection databaseConnection;
	private FileStoreConnection fileStoreConnection;
	private ElasticsearchConnection elasticsearchConnection;
	private MetadataTableConfiguration metadataTableConfiguration;
	private MetadataObjectConfiguration metadataObjectConfiguration;
	private ConfigFile configFile;
	private String logonType;
	private String name;
	private String type;
	private String category;
	private MetadataRepositoryCategoryConfiguration metadataRepositoryCategoryConfiguration;
	private String scope;
	private String group;

	// Constructors
	public MetadataRepositoryConfiguration(FrameworkConfiguration frameworkConfiguration,
			FrameworkControl frameworkControl, OracleDatabaseConnection oracleDatabaseConnection) {
		this.setFrameworkCrypto(new FrameworkCrypto());
		this.setConfigTools(frameworkControl);
		this.setFrameworkConfiguration(frameworkConfiguration);
		ObjectMapper objectMapper = new ObjectMapper();
		this.setDatabaseConnection(objectMapper.convertValue(oracleDatabaseConnection, DatabaseConnection.class));
		this.setType("oracle");
		this.setGroup("database");
		this.setMetadataTableConfiguration(new MetadataTableConfiguration(frameworkConfiguration,
				frameworkControl.getProperties(), this.getFrameworkConfiguration().getSettingConfiguration(),
				this.getType(), this.getMetadataRepositoryCategoryConfiguration()));
	}

	public MetadataRepositoryConfiguration(FrameworkConfiguration frameworkConfiguration,
			FrameworkControl frameworkControl, SqliteDatabaseConnection dcSQLiteConnection) {
		this.setFrameworkCrypto(new FrameworkCrypto());
		this.setConfigTools(frameworkControl);
		this.setFrameworkConfiguration(frameworkConfiguration);
		ObjectMapper objectMapper = new ObjectMapper();
		this.setDatabaseConnection(objectMapper.convertValue(dcSQLiteConnection, SqliteDatabaseConnection.class));
		this.setType("sqlite");
		this.setGroup("database");
		this.setMetadataTableConfiguration(new MetadataTableConfiguration(frameworkConfiguration,
				frameworkControl.getProperties(), this.getFrameworkConfiguration().getSettingConfiguration(),
				this.getType(), this.getMetadataRepositoryCategoryConfiguration()));
	}

	public MetadataRepositoryConfiguration(FrameworkConfiguration frameworkConfiguration,
			FrameworkControl frameworkControl, NetezzaDatabaseConnection netezzaDatabaseConnection) {
		this.setFrameworkCrypto(new FrameworkCrypto());
		this.setConfigTools(frameworkControl);
		this.setFrameworkConfiguration(frameworkConfiguration);
		ObjectMapper objectMapper = new ObjectMapper();
		this.setDatabaseConnection(objectMapper.convertValue(netezzaDatabaseConnection, DatabaseConnection.class));
		this.setType("netezza");
		this.setGroup("database");
		this.setMetadataTableConfiguration(new MetadataTableConfiguration(frameworkConfiguration,
				frameworkControl.getProperties(), this.getFrameworkConfiguration().getSettingConfiguration(),
				this.getType(), this.getMetadataRepositoryCategoryConfiguration()));
	}

	public MetadataRepositoryConfiguration(FrameworkConfiguration frameworkConfiguration,
			FrameworkControl frameworkControl, PostgresqlDatabaseConnection postgresqlDatabaseConnection) {
		this.setFrameworkCrypto(new FrameworkCrypto());
		this.setConfigTools(frameworkControl);
		this.setFrameworkConfiguration(frameworkConfiguration);
		ObjectMapper objectMapper = new ObjectMapper();
		this.setDatabaseConnection(objectMapper.convertValue(postgresqlDatabaseConnection, DatabaseConnection.class));
		this.setType("postgresql");
		this.setGroup("database");
		this.setMetadataTableConfiguration(new MetadataTableConfiguration(frameworkConfiguration,
				frameworkControl.getProperties(), this.getFrameworkConfiguration().getSettingConfiguration(),
				this.getType(), this.getMetadataRepositoryCategoryConfiguration()));
	}

	public MetadataRepositoryConfiguration(FrameworkConfiguration frameworkConfiguration,
			FrameworkControl frameworkControl, MetadataFileStoreConnection metadataFileStoreConnection) {
		this.setFrameworkCrypto(new FrameworkCrypto());
		this.setConfigTools(frameworkControl);
		this.setFrameworkConfiguration(frameworkConfiguration);
		ObjectMapper objectMapper = new ObjectMapper();
		this.setFileStoreConnection(objectMapper.convertValue(metadataFileStoreConnection, FileStoreConnection.class));
		this.setGroup("filestore");
	}

	public MetadataRepositoryConfiguration(FrameworkConfiguration frameworkConfiguration,
			FrameworkControl frameworkControl, ElasticsearchConnection elasticsearchConnection) {
		this.setFrameworkCrypto(new FrameworkCrypto());
		this.frameworkControl = frameworkControl;
		this.setFrameworkConfiguration(frameworkConfiguration);
		ObjectMapper objectMapper = new ObjectMapper();
		this.setElasticsearchConnection(
				objectMapper.convertValue(elasticsearchConnection, ElasticsearchConnection.class));
		this.setType("elasticsearch");
		this.setGroup("http");
	}

	public MetadataRepositoryConfiguration(FrameworkConfiguration frameworkConfiguration,
			FrameworkControl frameworkControl, ConfigFile configFile, String logonType) {
		this.setConfigFile(configFile);
		this.setLogonType(logonType);
		this.setFrameworkCrypto(new FrameworkCrypto());
		this.setConfigTools(frameworkControl);
		this.setFrameworkConfiguration(frameworkConfiguration);
		this.loadMetadataRepositoryConfiguration(this.getConfigFile(), this.getLogonType());
	}

	// Clone
	@Override
	public MetadataRepositoryConfiguration clone() {
		try {
			return (MetadataRepositoryConfiguration) super.clone();
		} catch (CloneNotSupportedException e) {
			return new MetadataRepositoryConfiguration(this.getFrameworkConfiguration(), this.getConfigTools(),
					this.getConfigFile(), this.getLogonType());
		}
	}

	// Methods
	private void loadMetadataRepositoryConfiguration(ConfigFile configFile, String logonType) {
		this.setName(configFile.getProperty(
				this.getFrameworkConfiguration().getSettingConfiguration().getSettingPath("metadata.repository.name"))
				.toLowerCase());
		this.setType(configFile.getProperty(
				this.getFrameworkConfiguration().getSettingConfiguration().getSettingPath("metadata.repository.type"))
				.toLowerCase());
		if (this.getType().equals("elasticsearch")) {
			this.setElasticsearchConnection(this.getElasticsearchConnectionConfiguration(configFile));
			this.setType("elasticsearch");
		} else if (this.getType().equals("sqlite")) {
			SqliteDatabaseConnection sqliteDatabaseConnection = this.getSqliteDatabaseConnection();
			ObjectMapper objectMapper = new ObjectMapper();
			this.databaseConnection = objectMapper.convertValue(sqliteDatabaseConnection,
					SqliteDatabaseConnection.class);
			this.setGroup("database");
		} else if (this.getType().equals("oracle")) {
			ObjectMapper objectMapper = new ObjectMapper();
			this.setDatabaseConnection(objectMapper.convertValue(this.getOracleDatabaseConnection(logonType),
					OracleDatabaseConnection.class));
			this.setGroup("database");
		} else if (this.getType().equals("netezza")) {
			ObjectMapper objectMapper = new ObjectMapper();
			this.setDatabaseConnection(objectMapper.convertValue(this.getNetezzaDatabaseConnection(logonType),
					NetezzaDatabaseConnection.class));
			this.setGroup("database");
		} else if (this.getType().equals("postgresql")) {
			ObjectMapper objectMapper = new ObjectMapper();
			this.setDatabaseConnection(objectMapper.convertValue(this.getPostgresqlDatabaseConnection(logonType),
					PostgresqlDatabaseConnection.class));
			this.setGroup("database");
		} else if (this.getType().equals("filestore")) {
			ObjectMapper objectMapper = new ObjectMapper();
			this.setFileStoreConnection(
					objectMapper.convertValue(this.getMetadataFileStoreConnection(), FileStoreConnection.class));
			this.setGroup("filestore");
		}

		// Set Category
		if (configFile.getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
				.getSettingPath("metadata.repository.category")) != null) {
			this.setCategory(configFile.getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
					.getSettingPath("metadata.repository.category")));
		} else {
			this.setCategory("");
		}

	}

	public void initCategoryConfiguration(String category) {
		this.setMetadataRepositoryCategoryConfiguration(new MetadataRepositoryCategoryConfiguration(category,
				this.getFrameworkConfiguration().getFolderConfiguration()));
		this.setMetadataTableConfiguration(new MetadataTableConfiguration(frameworkConfiguration,
				this.getConfigFile().getProperties(), this.getFrameworkConfiguration().getSettingConfiguration(),
				this.getType(), this.getMetadataRepositoryCategoryConfiguration()));
		this.setMetadataObjectConfiguration(
				new MetadataObjectConfiguration(this.getMetadataRepositoryCategoryConfiguration()));
	}

	// Metadata Repository configuration
	private OracleDatabaseConnection getOracleDatabaseConnection(String logonType) {
		String connectionURL = "";

		if (!this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
				.getSettingPath("metadata.repository.connection.string")).trim().isEmpty()) {
			connectionURL = this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
					.getSettingPath("metadata.repository.connection.string"));
		} else if (!this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
				.getSettingPath("metadata.repository.oracle.service")).trim().isEmpty()) {
			connectionURL = "jdbc:oracle:thin:@//"
					+ this.getConfigFile()
							.getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
									.getSettingPath("metadata.repository.oracle.host"))
					+ ":"
					+ this.getConfigFile()
							.getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
									.getSettingPath("metadata.repository.oracle.port"))
					+ "/" + this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
							.getSettingPath("metadata.repository.oracle.service"));
		} else {
			connectionURL = "jdbc:oracle:thin:@"
					+ this.getConfigFile()
							.getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
									.getSettingPath("metadata.repository.oracle.host"))
					+ ":"
					+ this.getConfigFile()
							.getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
									.getSettingPath("metadata.repository.oracle.port"))
					+ ":" + this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
							.getSettingPath("metadata.repository.oracle.tnsalias"));
		}
		// Get user name and password
		// Default user is readonly
		FrameworkCrypto passwordDecryptor = this.getFrameworkCrypto();
		String userName = "";
		String userPassword = null;
		if (logonType.equalsIgnoreCase("write")) {
			userName = this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
					.getSettingPath("metadata.repository.oracle.writer"));
			try {
				userPassword = passwordDecryptor.decrypt(
						this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
								.getSettingPath("metadata.repository.oracle.writer.password")));
			} catch (Exception e) {
				userPassword = "";
			}

		} else if (logonType.equalsIgnoreCase("owner")) {
			userName = this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
					.getSettingPath("metadata.repository.oracle.schema"));
			try {
				userPassword = passwordDecryptor.decrypt(
						this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
								.getSettingPath("metadata.repository.oracle.schema.password")));
			} catch (Exception e) {
				userPassword = "";
			}
		} else {
			userName = this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
					.getSettingPath("metadata.repository.oracle.reader"));
			try {
				userPassword = passwordDecryptor.decrypt(
						this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
								.getSettingPath("metadata.repository.oracle.reader.password")));
			} catch (Exception e) {
				userPassword = "";
			}

		}

		OracleDatabaseConnection oracleDatabaseConnection = new OracleDatabaseConnection(connectionURL, userName,
				userPassword);
		return oracleDatabaseConnection;
	}

	private NetezzaDatabaseConnection getNetezzaDatabaseConnection(String logonType) {
		String connectionURL = "";

		if (!this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
				.getSettingPath("metadata.repository.connection.string")).trim().isEmpty()) {
			connectionURL = this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
					.getSettingPath("metadata.repository.connection.string"));
		} else {
			connectionURL = "jdbc:netezza://"
					+ this.getConfigFile()
							.getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
									.getSettingPath("metadata.repository.netezza.host"))
					+ ":"
					+ this.getConfigFile()
							.getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
									.getSettingPath("metadata.repository.netezza.port"))
					+ "/" + this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
							.getSettingPath("metadata.repository.netezza.name"));
		}
		// Get user name and password
		// Default user is readonly
		FrameworkCrypto passwordDecryptor = this.getFrameworkCrypto();
		String userName = "";
		String userPassword = null;
		if (logonType.equalsIgnoreCase("write")) {
			userName = this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
					.getSettingPath("metadata.repository.netezza.writer"));
			try {
				userPassword = passwordDecryptor.decrypt(
						this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
								.getSettingPath("metadata.repository.netezza.writer.password")));
			} catch (Exception e) {
				userPassword = "";
			}

		} else if (logonType.equalsIgnoreCase("owner")) {
			userName = this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
					.getSettingPath("metadata.repository.netezza.schema.user"));
			try {
				userPassword = passwordDecryptor.decrypt(
						this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
								.getSettingPath("metadata.repository.netezza.schema.user.password")));
			} catch (Exception e) {
				userPassword = "";
			}
		} else {
			userName = this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
					.getSettingPath("metadata.repository.netezza.reader"));
			try {
				userPassword = passwordDecryptor.decrypt(
						this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
								.getSettingPath("metadata.repository.netezza.reader.password")));
			} catch (Exception e) {
				userPassword = "";
			}
		}

		NetezzaDatabaseConnection netezzaDatabaseConnection = new NetezzaDatabaseConnection(connectionURL, userName,
				userPassword);
		return netezzaDatabaseConnection;
	}

	private SqliteDatabaseConnection getSqliteDatabaseConnection() {
		String connectionURL = "";
		SqliteDatabaseConnection dcSQConnection = null;
		if (this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
				.getSettingPath("metadata.repository.connection.string")) != null && !this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
				.getSettingPath("metadata.repository.connection.string")).trim().isEmpty()) {
			connectionURL = this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
					.getSettingPath("metadata.repository.connection.string"));
			dcSQConnection = new SqliteDatabaseConnection(connectionURL, "", "");
		} else {
			dcSQConnection = new SqliteDatabaseConnection(
					this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
							.getSettingPath("metadata.repository.sqlite.file")));
		}
		return dcSQConnection;
	}

	private ElasticsearchConnection getElasticsearchConnectionConfiguration(ConfigFile configFile) {
		ElasticsearchConnection elasticsearchConnection = new ElasticsearchConnection(
				configFile.getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
						.getSettingPath("metadata.repository.elasticsearch.url")));
		return elasticsearchConnection;
	}

	private PostgresqlDatabaseConnection getPostgresqlDatabaseConnection(String logonType) {
		String connectionURL = "";

		if (!this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
				.getSettingPath("metadata.repository.connection.string")).trim().isEmpty()) {
			connectionURL = this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
					.getSettingPath("metadata.repository.connection.string"));
		} else {
			connectionURL = "jdbc:postgresql://"
					+ this.getConfigFile()
							.getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
									.getSettingPath("metadata.repository.postgresql.host"))
					+ ":"
					+ this.getConfigFile()
							.getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
									.getSettingPath("metadata.repository.postgresql.port"))
					+ "/" + this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
							.getSettingPath("metadata.repository.postgresql.name"));
		}
		// Get user name and password
		// Default user is readonly
		FrameworkCrypto passwordDecryptor = this.getFrameworkCrypto();
		String userName = "";
		String userPassword = null;
		if (logonType.equalsIgnoreCase("write")) {
			userName = this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
					.getSettingPath("metadata.repository.postgresql.writer"));
			try {
				userPassword = passwordDecryptor.decrypt(
						this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
								.getSettingPath("metadata.repository.postgresql.writer.password")));
			} catch (Exception e) {
				userPassword = "";
			}
		} else if (logonType.equalsIgnoreCase("owner")) {
			userName = this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
					.getSettingPath("metadata.repository.postgresql.schema.user"));
			try {
				userPassword = passwordDecryptor.decrypt(
						this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
								.getSettingPath("metadata.repository.postgresql.schema.user.password")));
			} catch (Exception e) {
				userPassword = "";
			}
		} else {
			userName = this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
					.getSettingPath("metadata.repository.postgresql.reader"));
			try {
				userPassword = passwordDecryptor.decrypt(
						this.getConfigFile().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
								.getSettingPath("metadata.repository.postgresql.reader.password")));
			} catch (Exception e) {
				userPassword = "";
			}
		}

		PostgresqlDatabaseConnection postgresqlDatabaseConnection = new PostgresqlDatabaseConnection(connectionURL,
				userName, userPassword);
		return postgresqlDatabaseConnection;
	}

	private MetadataFileStoreConnection getMetadataFileStoreConnection() {
		MetadataFileStoreConnection metadataFileStoreConnection = new MetadataFileStoreConnection(
				this.getConfigTools().getProperty(this.getFrameworkConfiguration().getSettingConfiguration()
						.getSettingPath("metadata.repository.filestore.path")));
		return metadataFileStoreConnection;
	}

	// Elasticsearch

	// Database
	public CachedRowSet executeQuery(String query) {
		CachedRowSet crs = null;
		try {
			crs = this.getDatabaseConnection().executeQuery(query);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return crs;
	}

	public void executeUpdate(String query) {
		this.getDatabaseConnection().executeUpdate(query);
	}

	public void executeScript(String fileName) {
		SqlScriptResult dcSQLScriptResult = this.getDatabaseConnection().executeScript(fileName);

		if (dcSQLScriptResult.getReturnCode() != 0) {
			throw new RuntimeException("Error execting SQL script");
		}
	}

	public void executeScript(InputStream inputStream) {
		SqlScriptResult dcSQLScriptResult = this.getDatabaseConnection().executeScript(inputStream);

		if (dcSQLScriptResult.getReturnCode() != 0) {
			throw new RuntimeException("Error executing SQL script");
		}
	}

	public void dropTable(String schemaName, String tableName) {
		String queryDropTable = "";
		if (schemaName.equals("")) {
			queryDropTable = "drop table " + tableName;
		} else {
			queryDropTable = "drop table " + schemaName + "." + tableName;
		}
		this.getDatabaseConnection().executeUpdate(queryDropTable);
	}

	public void cleanTable(String schemaName, String tableName) {
		String queryCleanTable = "";
		if (schemaName.equals("")) {
			queryCleanTable = "delete from " + tableName;
		} else {
			queryCleanTable = "delete from " + schemaName + "." + tableName;
		}
		this.getDatabaseConnection().executeUpdate(queryCleanTable);
	}

	public String getSystemTimestampExpression() {
		if (this.getDatabaseConnection().getType().equals("oracle")) {
			return "systimestamp";
		} else if (this.getDatabaseConnection().getType().equals("sqlite")) {
			return ("datetime(CURRENT_TIMESTAMP, 'localtime')");
		} else if (this.getDatabaseConnection().getType().equals("netezza")) {
			return "CURRENT_TIMESTAMP";
		} else if (this.getDatabaseConnection().getType().equals("postgresql")) {
			return "CURRENT_TIMESTAMP";
		} else {
			return "";
		}
	}

	// Getters and Setters
	public FrameworkControl getConfigTools() {
		return frameworkControl;
	}

	public void setConfigTools(FrameworkControl frameworkControl) {
		this.frameworkControl = frameworkControl;
	}

	public DatabaseConnection getDatabaseConnection() {
		return databaseConnection;
	}

	public void setDatabaseConnection(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public FileStoreConnection getFileStoreConnection() {
		return fileStoreConnection;
	}

	public void setFileStoreConnection(FileStoreConnection fileStoreConnection) {
		this.fileStoreConnection = fileStoreConnection;
	}

	public ElasticsearchConnection getElasticsearchConnection() {
		return elasticsearchConnection;
	}

	public void setElasticsearchConnection(ElasticsearchConnection elasticsearchConnection) {
		this.elasticsearchConnection = elasticsearchConnection;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MetadataRepositoryCategoryConfiguration getMetadataRepositoryCategoryConfiguration() {
		return metadataRepositoryCategoryConfiguration;
	}

	public void setMetadataRepositoryCategoryConfiguration(
			MetadataRepositoryCategoryConfiguration metadataRepositoryCategoryConfiguration) {
		this.metadataRepositoryCategoryConfiguration = metadataRepositoryCategoryConfiguration;
	}

	public ConfigFile getConfigFile() {
		return configFile;
	}

	public void setConfigFile(ConfigFile configFile) {
		this.configFile = configFile;
	}

	public String getLogonType() {
		return logonType;
	}

	public void setLogonType(String logonType) {
		this.logonType = logonType;
	}

	public FrameworkConfiguration getFrameworkConfiguration() {
		return frameworkConfiguration;
	}

	public void setFrameworkConfiguration(FrameworkConfiguration frameworkConfiguration) {
		this.frameworkConfiguration = frameworkConfiguration;
	}

	public FrameworkCrypto getFrameworkCrypto() {
		return frameworkCrypto;
	}

	public void setFrameworkCrypto(FrameworkCrypto frameworkCrypto) {
		this.frameworkCrypto = frameworkCrypto;
	}

	public MetadataObjectConfiguration getMetadataObjectConfiguration() {
		return metadataObjectConfiguration;
	}

	public void setMetadataObjectConfiguration(MetadataObjectConfiguration metadataObjectConfiguration) {
		this.metadataObjectConfiguration = metadataObjectConfiguration;
	}

	public MetadataTableConfiguration getMetadataTableConfiguration() {
		return metadataTableConfiguration;
	}

	public void setMetadataTableConfiguration(MetadataTableConfiguration metadataTableConfiguration) {
		this.metadataTableConfiguration = metadataTableConfiguration;
	}

}