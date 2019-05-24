package io.metadew.iesi.metadata.repository.coordinator.configuration;

import io.metadew.iesi.common.config.ConfigFile;
import io.metadew.iesi.framework.configuration.FrameworkSettingConfiguration;
import io.metadew.iesi.framework.crypto.FrameworkCrypto;

import java.text.MessageFormat;

public class RepositoryConfigurationFactory {

    public RepositoryConfiguration createRepositoryConfiguration(ConfigFile configFile, FrameworkSettingConfiguration frameworkSettingConfiguration, FrameworkCrypto frameworkCrypto) {
        if (!frameworkSettingConfiguration.getSettingPath("metadata.repository.type").isPresent()) {
            throw new RuntimeException("Unable to find settings path for 'metadata.repository.type'");
        }
        if (!configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.type").get()).isPresent()) {
            throw new RuntimeException("No repository type defined in configuration file");
        }
        if (configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.type").get()).get().equalsIgnoreCase("netezza")) {
            return new NetezzaRepositoryConfiguration(configFile, frameworkSettingConfiguration,frameworkCrypto);
        } else if (configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.type").get()).get().equalsIgnoreCase("h2")) {
            return new H2RepositoryConfiguration(configFile, frameworkSettingConfiguration,frameworkCrypto);
        } else if (configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.type").get()).get().equalsIgnoreCase("mssql")) {
            return new MssqlRepositoryConfiguration(configFile, frameworkSettingConfiguration,frameworkCrypto);
        } else if (configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.type").get()).get().equalsIgnoreCase("sqlite")) {
            return new SqliteRepositoryConfiguration(configFile, frameworkSettingConfiguration,frameworkCrypto);
        } else if (configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.type").get()).get().equalsIgnoreCase("oracle")) {
            return new OracleRepositoryConfiguration(configFile, frameworkSettingConfiguration,frameworkCrypto);
        } else if (configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.type").get()).get().equalsIgnoreCase("postgresql")) {
            return new PostgresqlRepositoryConfiguration(configFile, frameworkSettingConfiguration,frameworkCrypto);
        } else if (configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.type").get()).get().equalsIgnoreCase("filestore")) {
            return new FileStoreRepositoryConfiguration(configFile, frameworkSettingConfiguration,frameworkCrypto);
        } else if (configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.type").get()).get().equalsIgnoreCase("elasticsearch")) {
            return new ElasticSearchRepositoryConfiguration(configFile, frameworkSettingConfiguration,frameworkCrypto);
        } else {
            throw new RuntimeException(MessageFormat.format("Could not initiate reppository configuration for type {0}",
                    configFile.getProperty(frameworkSettingConfiguration.getSettingPath("metadata.repository.type").get()).get()));
		}
	}

}
