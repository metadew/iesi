package io.metadew.iesi.metadata.repository.coordinator.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;

import java.text.MessageFormat;
import java.util.Optional;

public class RepositoryConfigurationFactory {

    public RepositoryConfiguration createRepositoryConfiguration(ConfigFile configFile) {
        return getSettingValue(configFile, "metadata.repository.type")
                .map(type -> {
                    if (type.equalsIgnoreCase("netezza")) {
                        return new NetezzaRepositoryConfiguration(configFile);
                    } else if (type.equalsIgnoreCase("h2")) {
                        return new H2RepositoryConfiguration(configFile);
                    } else if (type.equalsIgnoreCase("mssql")) {
                        return new MssqlRepositoryConfiguration(configFile);
                    } else if (type.equalsIgnoreCase("sqlite")) {
                        return new SqliteRepositoryConfiguration(configFile);
                    } else if (type.equalsIgnoreCase("oracle")) {
                        return new OracleRepositoryConfiguration(configFile);
                    } else if (type.equalsIgnoreCase("postgresql")) {
                        return new PostgresqlRepositoryConfiguration(configFile);
                    } else if (type.equalsIgnoreCase("filestore")) {
                        return new FileStoreRepositoryConfiguration(configFile);
                    } else if (type.equalsIgnoreCase("elasticsearch")) {
                        return new ElasticSearchRepositoryConfiguration(configFile);
                    } else {
                        throw new RuntimeException(MessageFormat.format("Could not initiate reppository configuration for type {0}",
                                type));
                    }
                })
                .orElseThrow(() -> new RuntimeException("No repository type defined in configuration file"));
    }


    public Optional<String> getSettingValue(ConfigFile configFile, String settingPath) {
        return FrameworkSettingConfiguration.getInstance().getSettingPath(settingPath).flatMap(configFile::getProperty);
    }

}
