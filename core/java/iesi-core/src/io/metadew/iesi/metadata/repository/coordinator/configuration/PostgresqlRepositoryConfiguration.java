package io.metadew.iesi.metadata.repository.coordinator.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.PostgresqlDatabase;
import io.metadew.iesi.connection.database.connection.PostgresqlDatabaseConnection;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

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


    public PostgresqlRepositoryConfiguration(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {
        super(configFile, frameworkSettingConfiguration, frameworkCrypto);
    }

    @Override
    void fromConfigFile(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {
    	host = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.postgresql.host");
    	port = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.postgresql.port");
    	name = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.postgresql.name");
    	schema = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.postgresql.schema");
    	schemaUser = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.postgresql.schema.user");
    	schemaUserPassword = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.postgresql.schema.user.password");
    	writerUser = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.postgresql.writer");
    	writerUserPassword = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.postgresql.writer.password");
    	readerUser = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.postgresql.reader");
    	readerUserPassword = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.postgresql.reader.password");
    	jdbcConnectionString = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.connection.string");
    }

    @Override
    public RepositoryCoordinator toRepository() {
        Map<String, Database> databases = new HashMap<>();
        String actualJdbcConnectionString = "";
        if (getJdbcConnectionString().isPresent()) {
        	actualJdbcConnectionString = getJdbcConnectionString().get();
        } else {
        	actualJdbcConnectionString = PostgresqlDatabaseConnection.getConnectionUrl(getHost().orElse(""), Integer.parseInt(getPort().orElse("0")), getName().orElse(""));
        }

        final String finalJdbcConnectionString = actualJdbcConnectionString;
        getUser().ifPresent(owner -> {
            PostgresqlDatabaseConnection postgresqlDatabaseConnection = new PostgresqlDatabaseConnection(finalJdbcConnectionString, owner, getWriterPassword().orElse(""));
            getSchema().ifPresent(postgresqlDatabaseConnection::setSchema);
            PostgresqlDatabase postgresqlDatabase = new PostgresqlDatabase(postgresqlDatabaseConnection, getSchema().orElse(""));
            databases.put("owner", postgresqlDatabase);
            databases.put("writer", postgresqlDatabase);
            databases.put("reader", postgresqlDatabase);
        });

        getWriter().ifPresent(writer -> {
            PostgresqlDatabaseConnection postgresqlDatabaseConnection = new PostgresqlDatabaseConnection(finalJdbcConnectionString, writer, getWriterPassword().orElse(""));
            getSchema().ifPresent(postgresqlDatabaseConnection::setSchema);
            PostgresqlDatabase postgresqlDatabase = new PostgresqlDatabase(postgresqlDatabaseConnection, getSchema().orElse(""));
            databases.put("writer", postgresqlDatabase);
            databases.put("reader", postgresqlDatabase);
        });

        getReader().ifPresent(reader -> {
            PostgresqlDatabaseConnection postgresqlDatabaseConnection = new PostgresqlDatabaseConnection(finalJdbcConnectionString, reader, getReaderPassword().orElse(""));
            getSchema().ifPresent(postgresqlDatabaseConnection::setSchema);
            PostgresqlDatabase postgresqlDatabase = new PostgresqlDatabase(postgresqlDatabaseConnection, getSchema().orElse(""));
            databases.put("reader", postgresqlDatabase);
        });

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
