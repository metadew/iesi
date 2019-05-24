package io.metadew.iesi.metadata.repository.coordinator.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.connection.database.Database;
import io.metadew.iesi.connection.database.SqliteDatabase;
import io.metadew.iesi.connection.database.connection.SqliteDatabaseConnection;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;
import io.metadew.iesi.metadata.repository.coordinator.RepositoryCoordinator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SqliteRepositoryConfiguration extends RepositoryConfiguration {
    private final String jdbcConnectionStringFormat = "jdbc:sqlite:%s";

    private String jdbcConnectionString;
    private String file;


    public SqliteRepositoryConfiguration(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {
        super(configFile, frameworkSettingConfiguration, frameworkCrypto);
    }

    @Override
    void fromConfigFile(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {

        // get jdbc connection url
        if (frameworkSettingConfiguration.getSettingPath("metadata.repository.connection.string").isPresent() &&
                configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.connection.string").get()).isPresent()) {
            jdbcConnectionString = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.connection.string").get()).get();
        } else if (frameworkSettingConfiguration.getSettingPath("metadata.repository.sqlite.file").isPresent() &&
                configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.sqlite.file").get()).isPresent()) {

            file = configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.sqlite.file").get()).get();
            jdbcConnectionString = String.format(jdbcConnectionStringFormat, file);
        } else {
            throw new RuntimeException("Could not initialize Sqlite configuration. No connection string or host, port and name provided");
        }
    }

    @Override
    public RepositoryCoordinator toRepository() {
        Map<String, Database> databases = new HashMap<>();

            databases.put("owner", new SqliteDatabase(new SqliteDatabaseConnection(getJdbcConnectionString(), "", "")));
            databases.put("writer",  new SqliteDatabase(new SqliteDatabaseConnection(getJdbcConnectionString(), "", "")));
            databases.put("reader", new SqliteDatabase(new SqliteDatabaseConnection(getJdbcConnectionString(), "", "")));

        return new RepositoryCoordinator(databases);
    }

    public String getJdbcConnectionString() {
        return jdbcConnectionString;
    }

    public Optional<String> getFile() {
        return Optional.ofNullable(file);
    }
}
