package io.metadew.iesi.metadata.repository.coordinator.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.H2Database;
import io.metadew.iesi.connection.database.connection.H2DatabaseConnection;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
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


    public H2RepositoryConfiguration(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {
       super(configFile, frameworkSettingConfiguration, frameworkCrypto);
    }

    @Override
    void fromConfigFile(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {
    	host = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.h2.host");
    	port = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.h2.port");
    	schema = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.h2.schema");
    	ownerUser = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.h2.owner");
    	ownerUserPassword = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.h2.owner.password");
    	writerUser = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.h2.writer");
    	writerUserPassword = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.h2.writer.password");
    	readerUser = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.h2.reader");
    	readerUserPassword = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.h2.reader.password");
    	jdbcConnectionString = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.connection.string");    	
    }

    @Override
    public RepositoryCoordinator toRepository() {
        Map<String, Database> databases = new HashMap<>();
        String actualJdbcConnectionString = "";
        if (getJdbcConnectionString().isPresent()) {
            actualJdbcConnectionString = getJdbcConnectionString().get();
        } else {
            actualJdbcConnectionString = H2DatabaseConnection.getConnectionUrl(getHost().orElse(""), Integer.parseInt(getPort().orElse("0")), getFile().orElse(""));
        }
        final String finalJdbcConnectionString = actualJdbcConnectionString;

        if (getUser().isPresent()) {
            getUser().ifPresent(owner -> {
                H2DatabaseConnection h2DatabaseConnection = new H2DatabaseConnection(finalJdbcConnectionString, owner, getUserPassword().orElse(""));
                getSchema().ifPresent(h2DatabaseConnection::setSchema);
                H2Database h2Database = new H2Database(h2DatabaseConnection, getSchema().orElse(""));
                databases.put("owner", h2Database);
                databases.put("writer", h2Database);
                databases.put("reader", h2Database);
            });

            getWriter().ifPresent(writer -> {
                H2DatabaseConnection h2DatabaseConnection = new H2DatabaseConnection(finalJdbcConnectionString, writer, getWriterPassword().orElse(""));
                getSchema().ifPresent(h2DatabaseConnection::setSchema);
                H2Database h2Database = new H2Database(h2DatabaseConnection, getSchema().orElse(""));
                databases.put("writer", h2Database);
                databases.put("reader", h2Database);
            });

            getReader().ifPresent(reader -> {
                H2DatabaseConnection h2DatabaseConnection = new H2DatabaseConnection(finalJdbcConnectionString, reader, getReaderPassword().orElse(""));
                getSchema().ifPresent(h2DatabaseConnection::setSchema);
                H2Database h2Database = new H2Database(h2DatabaseConnection, getSchema().orElse(""));
                databases.put("reader", h2Database);
            });
        } else {
            H2DatabaseConnection h2DatabaseConnection = new H2DatabaseConnection(finalJdbcConnectionString, "", "");
            getSchema().ifPresent(h2DatabaseConnection::setSchema);
            H2Database h2Database = new H2Database(h2DatabaseConnection, getSchema().orElse(""));
            databases.put("owner", h2Database);
            databases.put("writer", h2Database);
            databases.put("reader", h2Database);
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

    public Optional<String> getFile() {
        return Optional.ofNullable(file);
    }

}
