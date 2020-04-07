package io.metadew.iesi.metadata.repository.coordinator.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.MysqlDatabase;
import io.metadew.iesi.connection.database.connection.mysql.MysqlDatabaseConnection;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MysqlRepositoryConfiguration extends RepositoryConfiguration {

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


    public MysqlRepositoryConfiguration(ConfigFile configFile) {
        super(configFile);
    }

    @Override
    void fromConfigFile(ConfigFile configFile) {
        host = getSettingValue(configFile, "metadata.repository.mysql.host");
        port = getSettingValue(configFile, "metadata.repository.mysql.port");
        name = getSettingValue(configFile, "metadata.repository.mysql.name");
        schema = getSettingValue(configFile, "metadata.repository.mysql.schema");
        schemaUser = getSettingValue(configFile, "metadata.repository.mysql.schema.user");
        schemaUserPassword = getSettingValue(configFile, "metadata.repository.mysql.schema.user.password");
        writerUser = getSettingValue(configFile, "metadata.repository.mysql.writer");
        writerUserPassword = getSettingValue(configFile, "metadata.repository.mysql.writer.password");
        readerUser = getSettingValue(configFile, "metadata.repository.mysql.reader");
        readerUserPassword = getSettingValue(configFile, "metadata.repository.mysql.reader.password");
        jdbcConnectionString = getSettingValue(configFile, "metadata.repository.connection.string");
    }

    @Override
    public RepositoryCoordinator toRepository()  {
        Map<String, Database> databases = new HashMap<>();
        String actualJdbcConnectionString = "";
        if (getJdbcConnectionString().isPresent()) {
            actualJdbcConnectionString = getJdbcConnectionString().get();
        } else {
            actualJdbcConnectionString = MysqlDatabaseConnection.getConnectionUrl(getHost().orElse(""), Integer.parseInt(getPort().orElse("0")), getName().orElse(""));
        }

        final String finalJdbcConnectionString = actualJdbcConnectionString;
        if (getUser().isPresent()) {
            MysqlDatabaseConnection mysqlDatabaseConnection = new MysqlDatabaseConnection(finalJdbcConnectionString, getUser().get(), FrameworkCrypto.getInstance().decrypt(getWriterPassword().orElse("")));
            getSchema().ifPresent(mysqlDatabaseConnection::setSchema);
            MysqlDatabase mysqlDatabase = new MysqlDatabase(mysqlDatabaseConnection, schema);
            databases.put("owner", mysqlDatabase);
            databases.put("writer", mysqlDatabase);
            databases.put("reader", mysqlDatabase);
        }
        if (getWriter().isPresent()) {
            MysqlDatabaseConnection mysqlDatabaseConnection = new MysqlDatabaseConnection(finalJdbcConnectionString, getWriter().get(), FrameworkCrypto.getInstance().decrypt(getWriterPassword().orElse("")));
            getSchema().ifPresent(mysqlDatabaseConnection::setSchema);
            MysqlDatabase mysqlDatabase = new MysqlDatabase(mysqlDatabaseConnection, schema);
            databases.put("writer", mysqlDatabase);
            databases.put("reader", mysqlDatabase);
        }
        if (getReader().isPresent()) {
            MysqlDatabaseConnection mysqlDatabaseConnection = new MysqlDatabaseConnection(finalJdbcConnectionString, getReader().get(), FrameworkCrypto.getInstance().decrypt(getReaderPassword().orElse("")));
            getSchema().ifPresent(mysqlDatabaseConnection::setSchema);
            MysqlDatabase mysqlDatabase = new MysqlDatabase(mysqlDatabaseConnection, schema);
            databases.put("reader", mysqlDatabase);
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
