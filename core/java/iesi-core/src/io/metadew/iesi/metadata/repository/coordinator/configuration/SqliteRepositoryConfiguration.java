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

    private String jdbcConnectionString;
    private String file;


    public SqliteRepositoryConfiguration(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {
        super(configFile, frameworkSettingConfiguration, frameworkCrypto);
    }

    @Override
    void fromConfigFile(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {
        file = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.sqlite.file");
        jdbcConnectionString = getSettingValue(frameworkSettingConfiguration, frameworkCrypto, configFile, "metadata.repository.connection.string");
    }

    @Override
    public RepositoryCoordinator toRepository() {
        Map<String, Database> databases = new HashMap<>();
        String actualJdbcConnectionString = "";
        if (getJdbcConnectionString().isPresent()) {
        	actualJdbcConnectionString = getJdbcConnectionString().get();
        } else {
        	actualJdbcConnectionString = SqliteDatabaseConnection.getConnectionUrl(getFile().orElse(""));
        }

        final String finalJdbcConnectionString = actualJdbcConnectionString;
        
        SqliteDatabaseConnection sqliteDatabaseConnection = new SqliteDatabaseConnection(finalJdbcConnectionString, "","");
        SqliteDatabase sqliteDatabase = new SqliteDatabase(sqliteDatabaseConnection);
        databases.put("owner", sqliteDatabase);
        databases.put("writer", sqliteDatabase);
        databases.put("reader", sqliteDatabase);

        return new RepositoryCoordinator(databases);
    }

    public Optional<String> getJdbcConnectionString() {
        return Optional.ofNullable(jdbcConnectionString);
    }

    public Optional<String> getFile() {
        return Optional.ofNullable(file);
    }
    
    
}
