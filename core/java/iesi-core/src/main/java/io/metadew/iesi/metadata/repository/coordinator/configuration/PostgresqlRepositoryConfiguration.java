package io.metadew.iesi.metadata.repository.coordinator.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.PostgresqlDatabase;
import io.metadew.iesi.connection.database.connection.postgresql.PostgresqlDatabaseConnection;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PostgresqlRepositoryConfiguration extends RepositoryConfiguration {

    private String jdbcConnectionString;
    private String host;
    private String port;
    private String name;
    private String schema;
    private String schemaUser;
    private String schemaUserPassword;
    private String writerUser;
    private String writerUserPassword;
    private String readerUser;
    private String readerUserPassword;


    public PostgresqlRepositoryConfiguration(ConfigFile configFile) {
        super(configFile);
    }

    @Override
    void fromConfigFile(ConfigFile configFile) {
    	host = getSettingValue(configFile, "metadata.repository.postgresql.host");
    	port = getSettingValue(configFile, "metadata.repository.postgresql.port");
    	name = getSettingValue(configFile, "metadata.repository.postgresql.name");
    	schema = getSettingValue(configFile, "metadata.repository.postgresql.schema");
    	schemaUser = getSettingValue(configFile, "metadata.repository.postgresql.schema.user");
    	schemaUserPassword = getSettingValue(configFile, "metadata.repository.postgresql.schema.user.password");
    	writerUser = getSettingValue(configFile, "metadata.repository.postgresql.writer");
    	writerUserPassword = getSettingValue(configFile, "metadata.repository.postgresql.writer.password");
    	readerUser = getSettingValue(configFile, "metadata.repository.postgresql.reader");
    	readerUserPassword = getSettingValue(configFile, "metadata.repository.postgresql.reader.password");
    	jdbcConnectionString = getSettingValue(configFile, "metadata.repository.connection.string");
    }

    @Override
    public RepositoryCoordinator toRepository()  {
        Map<String, Database> databases = new HashMap<>();
        String actualJdbcConnectionString = "";
        if (getJdbcConnectionString().isPresent()) {
        	actualJdbcConnectionString = getJdbcConnectionString().get();
        } else {
        	actualJdbcConnectionString = PostgresqlDatabaseConnection.getConnectionUrl(getHost().orElse(""), Integer.parseInt(getPort().orElse("0")), getName().orElse(""));
        }

        final String finalJdbcConnectionString = actualJdbcConnectionString;
        if (getUser().isPresent()) {
                PostgresqlDatabaseConnection postgresqlDatabaseConnection = new PostgresqlDatabaseConnection(finalJdbcConnectionString, getUser().get(), getWriterPassword().orElse(""));
                getSchema().ifPresent(postgresqlDatabaseConnection::setSchema);
                PostgresqlDatabase postgresqlDatabase = new PostgresqlDatabase(postgresqlDatabaseConnection, getSchema().orElse(""));
                databases.put("owner", postgresqlDatabase);
                databases.put("writer", postgresqlDatabase);
                databases.put("reader", postgresqlDatabase);
        }
        if (getWriter().isPresent()) {
            PostgresqlDatabaseConnection postgresqlDatabaseConnection = new PostgresqlDatabaseConnection(finalJdbcConnectionString, getWriter().get(), getWriterPassword().orElse(""));
            getSchema().ifPresent(postgresqlDatabaseConnection::setSchema);
            PostgresqlDatabase postgresqlDatabase = new PostgresqlDatabase(postgresqlDatabaseConnection, getSchema().orElse(""));
            databases.put("writer", postgresqlDatabase);
            databases.put("reader", postgresqlDatabase);
        }
         if (getReader().isPresent()) {
             PostgresqlDatabaseConnection postgresqlDatabaseConnection = new PostgresqlDatabaseConnection(finalJdbcConnectionString, getReader().get(), getReaderPassword().orElse(""));
             getSchema().ifPresent(postgresqlDatabaseConnection::setSchema);
             PostgresqlDatabase postgresqlDatabase = new PostgresqlDatabase(postgresqlDatabaseConnection, getSchema().orElse(""));
             databases.put("reader", postgresqlDatabase);
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

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getSchema() {
        return Optional.ofNullable(schema);
    }

    public Optional<String> getUser() {
        return Optional.ofNullable(schemaUser);
    }

    public Optional<String> getUserPassword() {
        return Optional.ofNullable(schemaUserPassword);
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
}
