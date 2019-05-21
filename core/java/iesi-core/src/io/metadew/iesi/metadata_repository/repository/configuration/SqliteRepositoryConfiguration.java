package io.metadew.iesi.metadata_repository.repository.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.metadata_repository.repository.Repository;
import io.metadew.iesi.metadata_repository.repository.database.Database;
import io.metadew.iesi.metadata_repository.repository.database.SqliteDatabase;
import io.metadew.iesi.metadata_repository.repository.database.connection.SqliteDatabaseConnection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SqliteRepositoryConfiguration extends RepositoryConfiguration {
    private final String jdbcConnectionStringFormat = "jdbc:sqlite:%s";

    private String jdbcConnectionString;
    private String file;


    public SqliteRepositoryConfiguration(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration) {
        super(configFile, frameworkSettingConfiguration);
    }

    @Override
    void fromConfigFile(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration) {

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
    public Repository toRepository() {
        Map<String, Database> databases = new HashMap<>();

        databases.put("owner", new SqliteDatabase(new SqliteDatabaseConnection(getJdbcConnectionString(), "", "")));
        databases.put("writer", new SqliteDatabase(new SqliteDatabaseConnection(getJdbcConnectionString(), "", "")));
        databases.put("reader", new SqliteDatabase(new SqliteDatabaseConnection(getJdbcConnectionString(), "", "")));

        return new Repository(databases);
    }

    public String getJdbcConnectionString() {
        return jdbcConnectionString;
    }

    public Optional<String> getFile() {
        return Optional.ofNullable(file);
    }
}
