package io.metadew.iesi.metadata.repository.coordinator.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.H2Database;
import io.metadew.iesi.connection.database.connection.h2.H2DatabaseConnection;
import io.metadew.iesi.connection.database.connection.h2.H2EmbeddedDatabaseConnection;
import io.metadew.iesi.connection.database.connection.h2.H2MemoryDatabaseConnection;
import io.metadew.iesi.connection.database.connection.h2.H2ServerDatabaseConnection;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class H2RepositoryConfiguration extends RepositoryConfiguration {
    private String jdbcConnectionString;
    private String host;
    private String port;
    private String file;
    private String schema;
    private String ownerUser;
    private String ownerUserPassword;
    private String writerUser;
    private String writerUserPassword;
    private String readerUser;
    private String readerUserPassword;
    private String type;
    private String databaseName;


    public H2RepositoryConfiguration(ConfigFile configFile) {
       super(configFile);
    }

    @Override
    void fromConfigFile(ConfigFile configFile) {
        host = getSettingValue(configFile, "metadata.repository.h2.host");
        databaseName = getSettingValue(configFile, "metadata.repository.h2.name");
        type = getSettingValue(configFile, "metadata.repository.h2.type");
        port = getSettingValue(configFile, "metadata.repository.h2.port");
        file = getSettingValue(configFile, "metadata.repository.h2.file");
    	schema = getSettingValue(configFile, "metadata.repository.h2.schema");
    	ownerUser = getSettingValue(configFile, "metadata.repository.h2.owner");
    	ownerUserPassword = getSettingValue(configFile, "metadata.repository.h2.owner.password");
    	writerUser = getSettingValue(configFile, "metadata.repository.h2.writer");
    	writerUserPassword = getSettingValue(configFile, "metadata.repository.h2.writer.password");
    	readerUser = getSettingValue(configFile, "metadata.repository.h2.reader");
    	readerUserPassword = getSettingValue(configFile, "metadata.repository.h2.reader.password");
    	jdbcConnectionString = getSettingValue(configFile, "metadata.repository.connection.string");    	
    }

    @Override
    public RepositoryCoordinator toRepository() {
        Map<String, Database> databases = new HashMap<>();
        if (getUser().isPresent()) {
            getUser().ifPresent(owner -> {
                H2DatabaseConnection h2DatabaseConnection = getH2DataBaseConnection(owner, getUserPassword().orElse(""));
                getSchema().ifPresent(h2DatabaseConnection::setSchema);
                H2Database h2Database = new H2Database(h2DatabaseConnection, getSchema().orElse(""));
                databases.put("owner", h2Database);
                databases.put("writer", h2Database);
                databases.put("reader", h2Database);
            });

            getWriter().ifPresent(writer -> {
                H2DatabaseConnection h2DatabaseConnection = getH2DataBaseConnection(writer, getUserPassword().orElse(""));
                getSchema().ifPresent(h2DatabaseConnection::setSchema);
                H2Database h2Database = new H2Database(h2DatabaseConnection, getSchema().orElse(""));
                databases.put("writer", h2Database);
                databases.put("reader", h2Database);
            });

            getReader().ifPresent(reader -> {
                H2DatabaseConnection h2DatabaseConnection = getH2DataBaseConnection(reader, getUserPassword().orElse(""));
                getSchema().ifPresent(h2DatabaseConnection::setSchema);
                H2Database h2Database = new H2Database(h2DatabaseConnection, getSchema().orElse(""));
                databases.put("reader", h2Database);
            });
        } else {
            H2DatabaseConnection h2DatabaseConnection = getH2DataBaseConnection("","");
            getSchema().ifPresent(h2DatabaseConnection::setSchema);
            H2Database h2Database = new H2Database(h2DatabaseConnection, getSchema().orElse(""));
            databases.put("owner", h2Database);
            databases.put("writer", h2Database);
            databases.put("reader", h2Database);
        }

        return new RepositoryCoordinator(databases);
    }

    private H2DatabaseConnection getH2DataBaseConnection(String user, String password) {
        switch (type) {
            case "embedded":
                return new H2EmbeddedDatabaseConnection(getFile().orElseThrow(RuntimeException::new), user, password);
            case "server":
                return new H2ServerDatabaseConnection(getHost().orElseThrow(RuntimeException::new), Integer.parseInt(getPort().orElseThrow(RuntimeException::new)), getFile().orElseThrow(RuntimeException::new), user, password);
            case "memory":
                return new H2MemoryDatabaseConnection(getDatabaseName().orElseThrow(RuntimeException::new), user, password);
            default:
                throw new RuntimeException();
        }
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

    public Optional<String> getFile() {
        return Optional.ofNullable(file);
    }

    public Optional<String> getDatabaseName() {
        return Optional.ofNullable(databaseName);
    }
}
