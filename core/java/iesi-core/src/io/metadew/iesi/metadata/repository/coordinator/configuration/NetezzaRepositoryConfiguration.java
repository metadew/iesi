package io.metadew.iesi.metadata.repository.coordinator.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.NetezzaDatabase;
import io.metadew.iesi.connection.database.connection.NetezzaDatabaseConnection;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NetezzaRepositoryConfiguration extends RepositoryConfiguration {
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

    public NetezzaRepositoryConfiguration(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {
        super(configFile, frameworkSettingConfiguration, frameworkCrypto);
    }

    @Override
    void fromConfigFile(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {
    	host= getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.netezza.host");
    	port= getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.netezza.port");
    	name= getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.netezza.name");
    	schema = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.netezza.schema");
    	schemaUser = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.netezza.schema.user");
    	schemaUserPassword = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.netezza.schema.password");
    	writerUser = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.netezza.writer");
    	writerUserPassword = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.netezza.writer.password");
    	readerUser = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.netezza.reader");
    	readerUserPassword = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.netezza.reader.password");
    	jdbcConnectionString = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.connection.string");
    }

    @Override
    public RepositoryCoordinator toRepository() {
        Map<String, Database> databases = new HashMap<>();
        String actualJdbcConnectionString = "";
        if (getJdbcConnectionString().isPresent()) {
        	actualJdbcConnectionString = getJdbcConnectionString().get();
        } else {
        	actualJdbcConnectionString = NetezzaDatabaseConnection.getConnectionUrl(getHost().orElse(""), Integer.parseInt(getPort().orElse("0")), getName().orElse(""));
        }

        final String finalJdbcConnectionString = actualJdbcConnectionString;
        getUser().ifPresent(owner -> {
            NetezzaDatabaseConnection netezzaDatabaseConnection = new NetezzaDatabaseConnection(finalJdbcConnectionString, owner, getUserPassword().orElse(""));
            getSchema().ifPresent(netezzaDatabaseConnection::setSchema);
            NetezzaDatabase netezzaDatabase = new NetezzaDatabase(netezzaDatabaseConnection, getSchema().orElse(""));
            databases.put("owner", netezzaDatabase);
            databases.put("writer", netezzaDatabase);
            databases.put("reader", netezzaDatabase);
        });

        getWriter().ifPresent(writer -> {
            NetezzaDatabaseConnection netezzaDatabaseConnection = new NetezzaDatabaseConnection(finalJdbcConnectionString, writer, getWriterPassword().orElse(""));
            getSchema().ifPresent(netezzaDatabaseConnection::setSchema);
            NetezzaDatabase netezzaDatabase = new NetezzaDatabase(netezzaDatabaseConnection, getSchema().orElse(""));
            databases.put("writer", netezzaDatabase);
            databases.put("reader", netezzaDatabase);
        });

        getReader().ifPresent(reader -> {
            NetezzaDatabaseConnection netezzaDatabaseConnection = new NetezzaDatabaseConnection(finalJdbcConnectionString, reader, getReaderPassword().orElse(""));
            getSchema().ifPresent(netezzaDatabaseConnection::setSchema);
            NetezzaDatabase netezzaDatabase = new NetezzaDatabase(netezzaDatabaseConnection, getSchema().orElse(""));
            databases.put("reader", netezzaDatabase);
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
