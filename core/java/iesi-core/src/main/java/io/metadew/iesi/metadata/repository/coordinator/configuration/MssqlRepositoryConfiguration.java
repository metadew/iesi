package io.metadew.iesi.metadata.repository.coordinator.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.MssqlDatabase;
import io.metadew.iesi.connection.database.connection.mssql.MssqlDatabaseConnection;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MssqlRepositoryConfiguration extends RepositoryConfiguration {
    private String jdbcConnectionString;
    private String host;
    private String port;
    private String database;
    private String schema;
    private String ownerUser;
    private String ownerUserPassword;
    private String writerUser;
    private String writerUserPassword;
    private String readerUser;
    private String readerUserPassword;


    public MssqlRepositoryConfiguration(ConfigFile configFile) {
       super(configFile);
    }

    @Override
    void fromConfigFile(ConfigFile configFile) {
    	host = getSettingValue(configFile, "metadata.repository.mssql.host");
    	port = getSettingValue(configFile, "metadata.repository.mssql.port");
    	database = getSettingValue(configFile, "metadata.repository.mssql.database");
    	schema = getSettingValue(configFile, "metadata.repository.mssql.schema");
    	ownerUser = getSettingValue(configFile, "metadata.repository.mssql.owner");
    	ownerUserPassword = getSettingValue(configFile, "metadata.repository.mssql.owner.password");
    	writerUser = getSettingValue(configFile, "metadata.repository.mssql.writer");
    	writerUserPassword = getSettingValue(configFile, "metadata.repository.mssql.writer.password");
    	readerUser = getSettingValue(configFile, "metadata.repository.mssql.reader");
    	readerUserPassword = getSettingValue(configFile, "metadata.repository.mssql.reader.password");
    	jdbcConnectionString = getSettingValue(configFile, "metadata.repository.connection.string");
    }

    @Override
    public RepositoryCoordinator toRepository() {
        Map<String, Database> databases = new HashMap<>();
        String actualJdbcConnectionString = "";
        if (getJdbcConnectionString().isPresent()) {
        	actualJdbcConnectionString = getJdbcConnectionString().get();
        } else {
        	actualJdbcConnectionString = MssqlDatabaseConnection.getConnectionUrl(getHost().orElse(""), Integer.parseInt(getPort().orElse("0")), getDatabase().orElse(""));
        }

        final String finalJdbcConnectionString = actualJdbcConnectionString;

        if(getUser().isPresent()) {
            MssqlDatabaseConnection mssqlDatabaseConnection = new MssqlDatabaseConnection(finalJdbcConnectionString, getUser().get(), getUserPassword().orElse(""));
            getSchema().ifPresent(mssqlDatabaseConnection::setSchema);
            MssqlDatabase mssqlDatabase = new MssqlDatabase(mssqlDatabaseConnection, getSchema().orElse(""));
            databases.put("owner", mssqlDatabase);
            databases.put("writer", mssqlDatabase);
            databases.put("reader", mssqlDatabase);
        }
        if (getWriter().isPresent()) {
            MssqlDatabaseConnection mssqlDatabaseConnection = new MssqlDatabaseConnection(finalJdbcConnectionString, getWriter().get(), getWriterPassword().orElse(""));
            getSchema().ifPresent(mssqlDatabaseConnection::setSchema);
            MssqlDatabase mssqlDatabase = new MssqlDatabase(mssqlDatabaseConnection, getSchema().orElse(""));
            databases.put("writer", mssqlDatabase);
            databases.put("reader", mssqlDatabase);
        }
        if (getReader().isPresent()) {
            	MssqlDatabaseConnection mssqlDatabaseConnection = new MssqlDatabaseConnection(finalJdbcConnectionString, getReader().get(), getReaderPassword().orElse(""));
                getSchema().ifPresent(mssqlDatabaseConnection::setSchema);
                MssqlDatabase mssqlDatabase = new MssqlDatabase(mssqlDatabaseConnection, getSchema().orElse(""));
                databases.put("reader", mssqlDatabase);
        }

        if (!getReader().isPresent() && ! getUser().isPresent() && !getWriter().isPresent()){
        	MssqlDatabaseConnection mssqlDatabaseConnection = new MssqlDatabaseConnection(finalJdbcConnectionString, "", "");
            getSchema().ifPresent(mssqlDatabaseConnection::setSchema);
            MssqlDatabase mssqlDatabase = new MssqlDatabase(mssqlDatabaseConnection, getSchema().orElse(""));
            databases.put("owner", mssqlDatabase);
            databases.put("writer", mssqlDatabase);
            databases.put("reader", mssqlDatabase);
        }
        

        return new RepositoryCoordinator(databases);
    }

    public Optional<String> getJdbcConnectionString() {
    	return Optional.ofNullable(jdbcConnectionString);
    }

    public Optional<String> getHost() {
        return Optional.ofNullable(host);
    }

    public Optional<String> getPort() {
        return Optional.ofNullable(port);
    }

    public Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }

    public Optional<String> getUser() {
        return Optional.ofNullable(ownerUser);
    }

    public Optional<String> getUserPassword() {
        return Optional.ofNullable(ownerUserPassword);
    }

    public Optional<String> getWriter() {
        return Optional.ofNullable(writerUser);
    }

    public Optional<String> getWriterPassword() {
        return Optional.ofNullable(writerUserPassword);
    }

    public Optional<String> getReader() {
        return Optional.ofNullable(readerUser);
    }

    public Optional<String> getReaderPassword() {
        return Optional.ofNullable(readerUserPassword);
    }

	public Optional<String> getDatabase() {
		return Optional.ofNullable(database);
	}

	public void setDatabase(String database) {
		this.database = database;
	}

}
